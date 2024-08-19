package com.finsera.presentation.fragments.transfer.antar_bank

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.finsera.common.utils.Constant
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentCekRekeningAntarBankFormBinding
import com.finsera.presentation.fragments.transfer.antar_bank.bottomsheet.PilihBankModalBottomSheet
import com.finsera.presentation.fragments.transfer.antar_bank.bundle.CekRekeningAntarBundle
import com.finsera.presentation.fragments.transfer.antar_bank.viewmodel.CekRekeningAntarViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel

class CekRekeningAntarBankFormFragment : Fragment() {
    private var _binding: FragmentCekRekeningAntarBankFormBinding? = null
    private val binding get() = _binding!!

    private val cekRekeningAntarViewModel by koinNavGraphViewModel<CekRekeningAntarViewModel>(R.id.finsera_app_navgraph)
    private var hasAnnouncedScreen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCekRekeningAntarBankFormBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPilihBankBottomSheet()
        handleBackButton()
        observer()
        handleNextBtn()
        setupAccountNumberAccessibility()

        if (!hasAnnouncedScreen) {
            view?.announceForAccessibility(getString(R.string.screen_pilih_bank))
            hasAnnouncedScreen = true
        }
    }

    private fun initPilihBankBottomSheet() {
        val pilihBankModalBottomSheet = PilihBankModalBottomSheet()
        pilihBankModalBottomSheet.isCancelable = false

        binding.etPilihBank.inputType = InputType.TYPE_NULL
        binding.etPilihBank.isFocusable = false
        binding.etPilihBank.isCursorVisible = false
        binding.etPilihBank.isClickable = true

        binding.etPilihBank.setOnClickListener {
            pilihBankModalBottomSheet.show(childFragmentManager, PilihBankModalBottomSheet.TAG)
            view?.announceForAccessibility(getString(R.string.desc_bank_option))
        }
    }

    private fun handleBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            cekRekeningAntarViewModel.resetBankDetails()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
                cekRekeningAntarViewModel.resetBankDetails()
            }
        })
    }

    private fun handleNextBtn() {
        binding.btnLanjut.setOnClickListener {
            val idBank = cekRekeningAntarViewModel.idBankSelected
            val noRekening = binding.etNomorRekening.editableText.toString()

            if(noRekening.isNotEmpty()) {
                cekRekeningAntarViewModel.cekRekeningAntar(idBank!!, noRekening)
                //Toast.makeText(requireActivity(), "$idBank $noRekening", Toast.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "Kolom Bank dan Nomor Rekening wajib diisi untuk melanjutkan", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cekRekeningAntarViewModel.namaBankSelected.observe(viewLifecycleOwner) { data ->
                    data?.let {
                        binding.etPilihBank.setText(it)
                        binding.layoutnomorrekening.visibility = View.VISIBLE
                    } ?: run {
                        binding.etPilihBank.setText("Pilih Bank")
                        binding.layoutnomorrekening.visibility = View.INVISIBLE
                    }
                }

                cekRekeningAntarViewModel.cekRekeningAntarUiState.collectLatest { uiState ->
                    if(uiState.isValid) {
//                        Toast.makeText(requireActivity(), "VALID", Toast.LENGTH_SHORT).show()
                        if(findNavController().currentDestination?.id == R.id.cekRekeningAntarBankFormFragment) {
                            val dataRekening = CekRekeningAntarBundle(uiState.data?.idBank, uiState.data?.namaBank, uiState.data?.recipientName, uiState.data?.accountnumRecipient)
                            val bundle = Bundle().apply {
                                putParcelable(Constant.DATA_REKENING_ANTAR_BUNDLE, dataRekening)
                            }

                            findNavController().navigate(R.id.action_cekRekeningAntarBankFormFragment_to_transferAntarBankForm, bundle)
                            cekRekeningAntarViewModel.redirectedToKonfirmasiForm()
                            Snackbar.make(requireView(), "Rekening ditemukan", Snackbar.LENGTH_SHORT).show()
                        }
                    }

                    uiState.message?.let {
                        Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                        cekRekeningAntarViewModel.cekRekeningMessageShown()
                    }

                    if(uiState.isLoading) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupAccountNumberAccessibility() {
        binding.etNomorRekening.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                val editText = host as? EditText
                editText?.text?.let { text ->
                    info.text = text.toString().map { it.toString() }.joinToString(" ")
                }
            }
        }

        binding.etNomorRekening.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    binding.etNomorRekening.announceForAccessibility(it.toString().map { char -> char.toString() }.joinToString(" "))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}