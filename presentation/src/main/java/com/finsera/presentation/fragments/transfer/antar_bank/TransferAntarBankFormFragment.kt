package com.finsera.presentation.fragments.transfer.antar_bank

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.marginEnd
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.finsera.common.utils.Constant
import com.finsera.common.utils.format.CurrencyFormatter
import com.finsera.common.utils.network.ConnectivityManager
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentTransferAntarBankFormBinding
import com.finsera.presentation.fragments.transfer.antar_bank.bundle.CekRekeningAntarBundle
import com.finsera.presentation.fragments.transfer.antar_bank.viewmodel.TransferAntarBankFormViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TransferAntarBankFormFragment : Fragment() {
    private var _binding: FragmentTransferAntarBankFormBinding? = null
    private val binding get() = _binding!!

    private val transferAntarBankFormViewModel : TransferAntarBankFormViewModel by inject()
    private val connectivityManager: ConnectivityManager by inject()

    private var idBank: Int? = null
    private var namaBank: String? = null
    private var noRekening: String? = null
    private var namaPemilikRekening: String? = null
    private var addToDaftarTersimpan : Boolean = false

    private var saldoRekening: Double? = null

    private var hasAnnouncedScreen = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransferAntarBankFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferAntarBankFormViewModel.getInfoSaldoSaya()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // handle observe checkbox
        binding.checkboxDaftarTersimpanSesama.setOnCheckedChangeListener { _, isChecked ->
            addToDaftarTersimpan = isChecked
        }
        binding.cardInfoRekening.icBank.setImageResource(R.drawable.ic_bank_selain_bca_form_transfer) // set icon to non bca

        getBundleDataRekening()
        observeDataSaldo()
        setupListeners()

        if (!hasAnnouncedScreen) {
            val announcement = getString(R.string.screen_form_transfer, namaPemilikRekening)
            view.announceForAccessibility(announcement)
            hasAnnouncedScreen = true
        }
    }

    private fun getBundleDataRekening() {
        val bundle = arguments?.getParcelable<CekRekeningAntarBundle>(Constant.DATA_REKENING_ANTAR_BUNDLE)
        val savedItemMode = arguments?.getBoolean(Constant.DAFTAR_TERSIMPAN_SELECTED_MODE)

        if(savedItemMode == true) {
            binding.layoutdaftartersimpan.visibility = View.GONE
        }

        if(bundle != null) {
            noRekening = bundle.noRekening
            namaBank = bundle.namaBank
            idBank = bundle.idBank
            namaPemilikRekening = bundle.namaPemilikRekening

            binding.cardInfoRekening.tvNoRekening.text = noRekening
            binding.cardInfoRekening.tvNamaPenerima.text = namaPemilikRekening
            binding.cardInfoRekening.tvNamaBank.text = "Bank $namaBank"
            noRekening?.let { setAccountNumberContentDescription(it) }
            updateRecipientInfoAccessibility()
        }
    }

    private fun observeDataSaldo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferAntarBankFormViewModel.transferAntarFormUiState.collectLatest { uiState ->
                    uiState.message?.let {
                        Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                        transferAntarBankFormViewModel.messageFormShown()
                    }

                    if(uiState.isDataSaldoLoading) {
                        binding.cardInfoSaldo.tvSaldoRekening.visibility = View.INVISIBLE
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.visibility = View.VISIBLE
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.startShimmer()
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.visibility = View.INVISIBLE
                        binding.cardInfoSaldo.tvSaldoRekening.visibility = View.VISIBLE
                        binding.cardInfoSaldo.tvSaldoRekeningLoading.stopShimmer()
                        binding.progressBar.visibility = View.GONE
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
                            transferAntarBankFormViewModel.getInfoSaldoSaya()
                        }
                    } else {
                        binding.cardInfoSaldo.tvSaldoRekening.setTextColor(Color.BLACK)
                        binding.cardInfoSaldo.tvSaldoRekening.setOnClickListener { null }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        val dataRekening = CekRekeningAntarBundle(idBank, namaBank, namaPemilikRekening, noRekening)
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
    }

    private fun navigateToFormKonfirmasi(dataRekening: CekRekeningAntarBundle) {
        transferAntarBankFormViewModel.getInfoSaldoSaya()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferAntarBankFormViewModel.transferAntarFormUiState.collectLatest { uiState ->
                    if(uiState.isDataSaldoReady) {
                        if(findNavController().currentDestination?.id == R.id.transferAntarBankForm) {
                            val nominalTransfer = binding.etNominal.text.toString()
                            val catatanTransfer = if(binding.etCatatan.text.toString().isEmpty()) "-" else binding.etCatatan.text.toString()

                            val bundle = Bundle().apply {
                                putParcelable(Constant.DATA_REKENING_ANTAR_BUNDLE, dataRekening)
                                putString(Constant.NOMINAL_TRANSFER_EXTRA, nominalTransfer)
                                putString(Constant.CATATAN_TRANSFER_EXTRA, catatanTransfer)
                                putBoolean(Constant.DAFTAR_TERSIMPAN_CHECKED_EXTRA, addToDaftarTersimpan)
                            }

                            try {
                                if(nominalTransfer.toInt() > 10000) {
                                    if(nominalTransfer.toInt().toDouble() < saldoRekening!!) {
                                        findNavController().navigate(R.id.action_transferAntarBankForm_to_transferAntarBankFormKonfirmasi, bundle)
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

    private fun setAccountNumberContentDescription(accountNumber: String) {
        val spaceDelimitedNumber = accountNumber.map { it.toString() }.joinToString(" ")
        binding.cardInfoRekening.tvNoRekening.contentDescription = spaceDelimitedNumber
    }

    private fun updateSaldoAndaAccessibility(formattedSaldo: String) {
        val fullDescription = getString(R.string.desc_saldo_anda, formattedSaldo)
        binding.cardInfoSaldo.saldoAndaContainer.contentDescription = fullDescription
    }

    private fun updateRecipientInfoAccessibility() {
        val spaceDelimitedNumber = noRekening?.map { it.toString() }?.joinToString(" ") ?: ""
        val fullDescription = getString(
            R.string.desc_recipient_info,
            namaPemilikRekening ?: "",
            namaBank ?: "",
            spaceDelimitedNumber
        )
        binding.cardInfoRekening.root.contentDescription = fullDescription
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}