package com.finsera.presentation.fragments.transfer.antar_bank

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
import com.finsera.common.utils.format.CurrencyFormatter
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentTransferAntarBankFormKonfirmasiBinding
import com.finsera.presentation.fragments.transfer.antar_bank.bundle.CekRekeningAntarBundle
import com.finsera.presentation.fragments.transfer.antar_bank.viewmodel.TransferAntarBankViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TransferAntarBankFormKonfirmasiFragment : Fragment() {
    private var _binding: FragmentTransferAntarBankFormKonfirmasiBinding? = null
    private val binding get() = _binding!!

    private val transferAntarBankViewModel : TransferAntarBankViewModel by inject()

    private var namaPemilikRekening: String? = null
    private var noRekening: String? = null
    private var idBank: Int? = null
    private var namaBank: String? = null

    private var addToDaftarTersimpan: Boolean = false

    private var hasAnnouncedScreen = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransferAntarBankFormKonfirmasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        getBundle()

        if (!hasAnnouncedScreen) {
            view.announceForAccessibility(getString(R.string.screen_confirm_transaction))
            hasAnnouncedScreen = true
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        observer()
    }

    private fun getBundle() {
        val dataRekeningBundle = arguments?.getParcelable<CekRekeningAntarBundle>(Constant.DATA_REKENING_ANTAR_BUNDLE)
        val nominalTransfer = arguments?.getString(Constant.NOMINAL_TRANSFER_EXTRA)
        val catatanTransfer = arguments?.getString(Constant.CATATAN_TRANSFER_EXTRA)
        addToDaftarTersimpan = requireArguments().getBoolean(Constant.DAFTAR_TERSIMPAN_CHECKED_EXTRA)

        if(dataRekeningBundle != null && nominalTransfer != null) {
            val biayaAdmin = 2500
            idBank = dataRekeningBundle.idBank
            namaPemilikRekening = dataRekeningBundle.namaPemilikRekening
            noRekening = dataRekeningBundle.noRekening
            namaBank = dataRekeningBundle.namaBank

            binding.tvBankTujuan.text = "Bank $namaBank"
            binding.tvRekeningTujuan.text = noRekening
            binding.tvNamaPemilikRekeningTujuan.text = namaPemilikRekening
            binding.tvCatatanTf.text = catatanTransfer ?: "-"
            binding.tvNominalAwal.text = "Rp ${CurrencyFormatter.formatCurrency(nominalTransfer.toInt().toDouble())}"
            binding.tvBiayaAdmin.text = "Rp ${CurrencyFormatter.formatCurrency(biayaAdmin.toDouble())}"
            binding.tvNominalTotal.text = "Rp ${CurrencyFormatter.formatCurrency(nominalTransfer.toInt().toDouble() + biayaAdmin)}"

            setAccessibilityDescriptions()

            handleTransfer(nominalTransfer, catatanTransfer)
        }
    }

    private fun handleTransfer(nominalTransfer: String?, catatanTransfer: String?) {
        binding.btnNext.setOnClickListener {
            if(binding.etPinTransaksi.editText?.text.toString().isNotEmpty()) {
                transferAntarBankViewModel.transferAntar(idBank!!, noRekening!!, nominalTransfer!!.toInt().toDouble(), catatanTransfer ?: "-", binding.etPinTransaksi.editText?.text.toString())
            } else {
                Snackbar.make(requireView(), "PIN Transaksi harus diisi!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferAntarBankViewModel.transferAntarFormKonfirmasiUiState.collectLatest { uiState ->
                    uiState.message?.let { message ->
                        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                        transferAntarBankViewModel.messageFormKonfirmasiShown()
                    }

                    if(uiState.isLoading) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }

                    if(uiState.isSuccess) {
                        if (findNavController().currentDestination?.id == R.id.transferAntarBankFormKonfirmasi) {
                            val bundle = Bundle().apply {
                                putString(Constant.NAMA_NASABAH, transferAntarBankViewModel.userInfo?.first)
                                putString(Constant.NOMOR_REKENING_NASABAH, transferAntarBankViewModel.userInfo?.second)
                                putParcelable(Constant.TRANSFER_ANTAR_BERHASIL_BUNDLE, uiState.data)
                            }

                            if(addToDaftarTersimpan) {
                                transferAntarBankViewModel.simpanKeDaftarTersimpanAntar(idBank!!, namaBank!!, namaPemilikRekening!!,noRekening!!)
                                Snackbar.make(requireView(), "Transfer Berhasil dan Rekening berhasil ditambahkan ke Daftar Tersimpan.", Snackbar.LENGTH_SHORT).show()
                            } else {
                                Snackbar.make(requireView(), "Transfer Berhasil.", Snackbar.LENGTH_SHORT).show()
                            }

                            findNavController().navigate(R.id.action_transferAntarBankFormKonfirmasi_to_transferAntarBankSuksesFragment, bundle)
                            transferAntarBankViewModel.transferAntarBerhasil()
                        }
                    }
                }
            }
        }
    }

    private fun formatAccountNumberForAccessibility(accountNumber: String): String {
        return accountNumber.map { it.toString() }.joinToString(" ")
    }
    private fun setAccessibilityDescriptions() {
        binding.apply {
            val formattedAccountNumber = formatAccountNumberForAccessibility(tvRekeningTujuan.text.toString())
            layoutbanktujuan.contentDescription = getString(R.string.bank_tujuan_desc, tvBankTujuan.text)
            layoutnomorrekening.contentDescription = getString(R.string.rekening_tujuan_desc, formattedAccountNumber)
            layoutnamapemilikrekening.contentDescription = getString(R.string.nama_penerima_desc, tvNamaPemilikRekeningTujuan.text)
            layoutcatatan.contentDescription = getString(R.string.catatan_desc, tvCatatanTf.text)
            layoutnominal.contentDescription = getString(R.string.nominal_transfer_desc, tvNominalAwal.text)
            layoutbiayaadmin.contentDescription = getString(R.string.biaya_admin_desc, tvBiayaAdmin.text)
            layoutnominaltotal.contentDescription = getString(R.string.nominal_total_desc, tvNominalTotal.text)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}