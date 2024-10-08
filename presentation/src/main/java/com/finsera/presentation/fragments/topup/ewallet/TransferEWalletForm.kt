package com.finsera.presentation.fragments.topup.ewallet

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.finsera.common.utils.Constant
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentTransferEWalletFormBinding
import com.finsera.presentation.fragments.topup.ewallet.bundle.CekEWalletBundle
import com.finsera.presentation.fragments.topup.ewallet.bundle.ChooseEWalletBundle
import com.finsera.presentation.fragments.topup.ewallet.viewmodel.CheckEWalletViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferEWalletForm : Fragment() {
    private var _binding: FragmentTransferEWalletFormBinding? = null
    private val binding get() = _binding!!

    private var ewalletId: Int = 0
    private lateinit var ewalletName: String
    private var addToDaftarTersimpan: Boolean = false

    private val viewModel: CheckEWalletViewModel by viewModel()

    private var hasAnnouncedScreen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferEWalletFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()

        val bundle = arguments?.getParcelable<ChooseEWalletBundle>(Constant.DATA_ID_EWALLET)
        if (bundle != null) {
            ewalletName = bundle.ewalletName
            ewalletId = bundle.ewalletId
        }
        binding.eWalletItem.tvEwalletName.text = ewalletName
        setLogoEWallet(ewalletName)

        viewModel.resetState()
        binding.btnLanjut.setOnClickListener {
            val ewalletNum = binding.nomorEWalletEditText.text.toString()
            if (ewalletNum.isNotEmpty()) {
                viewModel.cekEWallet(ewalletId, ewalletNum).also {
                    observer()
                }
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.empty_ewallet_text, ewalletName),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.cbSave.setOnCheckedChangeListener { _, isChecked ->
            addToDaftarTersimpan = isChecked
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupAccessibility()
        binding.nomorEWalletEditText.inputType = InputType.TYPE_CLASS_NUMBER

        if (!hasAnnouncedScreen) {
            val announcement = getString(R.string.screen_isi_ewallet, ewalletName)
            view.announceForAccessibility(announcement)
            hasAnnouncedScreen = true
        }
    }

    private fun setupAccessibility() {
        binding.nomorEWalletEditText.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                val editText = host as? EditText
                editText?.text?.let { text ->
                    info.text = text.toString().map { it.toString() }.joinToString(" ")
                }
            }
        }

        binding.nomorEWalletEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    binding.nomorEWalletEditText.announceForAccessibility(it.toString().map { char -> char.toString() }.joinToString(" "))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        val fullDescription = getString(R.string.e_wallet_desc, ewalletName)
        binding.eWalletItem.root.contentDescription = fullDescription

    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cekEWalletUiState.collectLatest { uiState ->
                    uiState.message?.let { message ->
                        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                        viewModel.messageShown()
                    }

                    if (uiState.isLoading) {
                        binding.viewFlipper.displayedChild = 1
                    } else {
                        binding.viewFlipper.displayedChild = 0
                    }

                    if (uiState.isValid) {
                        Snackbar.make(
                            requireView(),
                            getString(R.string.e_wallet_ditemukan, ewalletName),
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        if (findNavController().currentDestination?.id == R.id.transferEWalletForm) {
                            val dataEWallet = CekEWalletBundle(
                                id = ewalletId,
                                nomorEWallet = uiState.data?.nomorAkunEwallet.toString(),
                                namaAkunEWallet = uiState.data?.namaAkunEwallet.toString(),
                                namaEWallet = ewalletName
                            )


                            val bundle = Bundle().apply {
                                putParcelable(Constant.DATA_CEK_EWALLET, dataEWallet)
                                putBoolean(
                                    Constant.DAFTAR_TERSIMPAN_CHECKED_EXTRA,
                                    addToDaftarTersimpan
                                )
                            }

                            findNavController().navigate(
                                R.id.action_transferEWalletForm_to_transferEWalletFormKonfirmasiFragment,
                                bundle
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setLogoEWallet(ewalletName: String) {
        when (ewalletName) {
            "OVO" -> binding.eWalletItem.ivIcon.setImageResource(R.drawable.ic_ovo)
            "Dana" -> binding.eWalletItem.ivIcon.setImageResource(R.drawable.ic_dana)
            "Paypal" -> binding.eWalletItem.ivIcon.setImageResource(R.drawable.ic_paypal)
            "GoPay" -> binding.eWalletItem.ivIcon.setImageResource(R.drawable.ic_gopay)
            "ShopeePay" -> binding.eWalletItem.ivIcon.setImageResource(R.drawable.ic_shopeepay)
        }
    }

    private fun setupListeners() {
        binding.btnLanjut.setOnClickListener {
            findNavController().navigate(R.id.action_transferEWalletForm_to_transferEWalletFormKonfirmasiFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}