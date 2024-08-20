package com.finsera.presentation.fragments.transfer.sesama_bank

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.finsera.common.utils.Constant
import com.finsera.common.utils.DisableTouchEvent
import com.finsera.common.utils.format.CurrencyFormatter
import com.finsera.common.utils.network.ConnectivityManager
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentTransferSesamaBankFormBinding
import com.finsera.presentation.fragments.transfer.sesama_bank.bundle.CekRekeningSesamaBundle
import com.finsera.presentation.fragments.transfer.sesama_bank.viewmodel.TransferSesamaBankFormViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TransferSesamaBankFormFragment : Fragment() {
    private var _binding :  FragmentTransferSesamaBankFormBinding? = null
    private val binding get() = _binding!!

    private val transferSesamaBankFormViewModel : TransferSesamaBankFormViewModel by inject()
    private val connectivityManager : ConnectivityManager by inject()

    private lateinit var noRekening : String
    private lateinit var namaPemilikRekening : String
    private var addToDaftarTersimpan : Boolean = false

    private var saldoRekening: Double? = null

    private var hasAnnouncedScreen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTransferSesamaBankFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferSesamaBankFormViewModel.getInfoSaldoSaya()
        observeDataSaldo()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.checkboxDaftarTersimpanSesama.setOnCheckedChangeListener { _, isChecked ->
            addToDaftarTersimpan = isChecked
        }

        val bundle = arguments?.getParcelable<CekRekeningSesamaBundle>(Constant.DATA_REKENING_SESAMA_BUNDLE)
        val savedItemMode = arguments?.getBoolean(Constant.DAFTAR_TERSIMPAN_SELECTED_MODE)

        if(bundle != null) {
            noRekening = bundle.noRekening
            namaPemilikRekening = bundle.namaPemilikRekening
            binding.cardInfoRekening.tvNoRekening.text = noRekening
            binding.cardInfoRekening.tvNamaPenerima.text = namaPemilikRekening
            setAccountNumberContentDescription(noRekening)
            updateRecipientInfoAccessibility()
        }

        if(savedItemMode == true) {
            binding.layoutdaftartersimpan.visibility = View.GONE
        }

        val dataRekening = CekRekeningSesamaBundle(namaPemilikRekening, noRekening)
        binding.btnNext.setOnClickListener {
            if(connectivityManager.hasInternetConnection()) {
                if(binding.etNominal.text.toString().isNotEmpty()) {
                    navigateToFormKonfirmasi(dataRekening)
                } else {
                    Toast.makeText(requireActivity(), "Isi Kolom Nominal Transfer Terlebih Dahulu!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(requireView(), "Tidak ada koneksi internet.", Snackbar.LENGTH_SHORT).show()
            }
        }

        if (!hasAnnouncedScreen) {
            val announcement = getString(R.string.screen_form_transfer, namaPemilikRekening)
            view.announceForAccessibility(announcement)
            hasAnnouncedScreen = true
        }

    }

    private fun observeDataSaldo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferSesamaBankFormViewModel.transferSesamaFormUiState.collectLatest { uiState ->
                    uiState.message?.let {
                        Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                        transferSesamaBankFormViewModel.messageFormShown()
                    }

                    if(uiState.isDataSaldoLoading) {
                        binding.cardInfoSaldo.tvSaldoRekening.visibility = View.INVISIBLE
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.visibility = View.VISIBLE
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.startShimmer()
                        binding.progressBar.visibility = View.VISIBLE

                        DisableTouchEvent.setInteractionDisabled(requireActivity(), true)
                    } else {
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.visibility = View.INVISIBLE
                        binding.cardInfoSaldo.tvSaldoRekening.visibility = View.VISIBLE
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.stopShimmer()
                        binding.progressBar.visibility = View.GONE

                        DisableTouchEvent.setInteractionDisabled(requireActivity(), false)
                    }

                    if(uiState.isDataSaldoReady) {
                        saldoRekening = uiState.dataSaldo?.amount
                        val formattedSaldo = "Rp" + CurrencyFormatter.formatCurrency(saldoRekening!!)
                        binding.cardInfoSaldo.tvSaldoRekening.text = formattedSaldo
                        updateSaldoAndaAccessibility(formattedSaldo)
                    }

                    if(!uiState.hasInternet) {
                        binding.cardInfoSaldo.tvSaldoRekening.text = "Tidak Ada Koneksi Internet. Klik untuk coba lagi."
                        binding.cardInfoSaldo.tvSaldoRekening.setTextColor(Color.RED)
                        binding.cardInfoSaldo.tvSaldoRekening.setOnClickListener {
                            transferSesamaBankFormViewModel.getInfoSaldoSaya()
                        }
                    } else {
                        binding.cardInfoSaldo.tvSaldoRekening.setTextColor(Color.BLACK)
                        binding.cardInfoSaldo.tvSaldoRekening.setOnClickListener { null }
                    }
                }
            }
        }
    }

    private fun setAccountNumberContentDescription(accountNumber: String) {
        val spaceDelimitedNumber = accountNumber.map { it.toString() }.joinToString(" ")
        binding.cardInfoRekening.tvNoRekening.contentDescription = spaceDelimitedNumber
    }

    private fun updateSaldoAndaAccessibility(formattedSaldo: String) {
        val fullDescription = getString(R.string.desc_saldo_anda, formattedSaldo)
        binding.cardInfoSaldo.saldoAndaContainer.contentDescription = fullDescription
    }

    private fun updateRecipientInfoAccessibility() {
        val spaceDelimitedNumber = noRekening.map { it.toString() }.joinToString(" ")
        val fullDescription = getString(
            R.string.desc_recipient_info,
            namaPemilikRekening,
            "BCA",
            spaceDelimitedNumber
        )
        binding.cardInfoRekening.root.contentDescription = fullDescription
    }

    private fun navigateToFormKonfirmasi(dataRekening: CekRekeningSesamaBundle) {
        transferSesamaBankFormViewModel.getInfoSaldoSaya()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferSesamaBankFormViewModel.transferSesamaFormUiState.collectLatest { uiState ->
                    if(uiState.isDataSaldoReady) {
                        if(findNavController().currentDestination?.id == R.id.transferSesamaBankFormFragment) {
                            val nominalTransfer = binding.etNominal.text.toString()
                            val catatanTransfer = if(binding.etCatatan.text.toString().isEmpty()) "-" else binding.etCatatan.text.toString()

                            val bundle = Bundle().apply {
                                putParcelable(Constant.DATA_REKENING_SESAMA_BUNDLE, dataRekening)
                                putString(Constant.NOMINAL_TRANSFER_EXTRA, nominalTransfer)
                                putString(Constant.CATATAN_TRANSFER_EXTRA, catatanTransfer)
                                putBoolean(Constant.DAFTAR_TERSIMPAN_CHECKED_EXTRA, addToDaftarTersimpan)
                            }

                            try {
                                if(nominalTransfer.toInt() > 10000) {
                                    if(nominalTransfer.toInt().toDouble() < saldoRekening!!) {
                                        findNavController().navigate(R.id.action_transferSesamaBankFormFragment_to_transferSesamaBankFormKonfirmasiFragment, bundle)
                                    } else {
                                        Snackbar.make(requireView(), "Saldo anda tidak mencukupi. Nominal harus lebih kecil atau sama dengan saldo di rekening anda.", Snackbar.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Snackbar.make(requireView(), "Nominal transfer harus lebih dari Rp10.000", Snackbar.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Snackbar.make(requireView(), "Maksimal transaksi dibatasi yaitu Rp1.000.000.000 per transaksinya", Snackbar.LENGTH_SHORT).show()
                                binding.etNominal.editableText.clear()
                            }

                        }
                    }
                }
            }
        }
    }
}

