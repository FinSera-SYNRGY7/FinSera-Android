package com.finsera.presentation.fragments.home

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.finsera.common.utils.extension.copyToClipboard
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.finsera.common.utils.format.CurrencyFormatter
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentHomeBinding
import com.finsera.presentation.fragments.home.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment(), TextToSpeech.OnInitListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var tts: TextToSpeech

    private lateinit var accessibilityManager: AccessibilityManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        setupAccessibility()

        accessibilityManager = requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        binding.cardNasabahInfo.tvNamaNasabah.setOnClickListener {
            speakAccountName()
        }

        binding.cardNasabahInfo.tvNoRekeningCard.setOnClickListener {
            speakAccountNumber()
        }

        val btnInfoSaldo = view.findViewById<ConstraintLayout>(R.id.btn_menu_infosaldo)
        btnInfoSaldo.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_infoSaldoFragment)
        }

        val btnTransferSesama = view.findViewById<ConstraintLayout>(R.id.btn_menu_transfer_sesama)
        btnTransferSesama.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transferSesamaBankHome)
        }

        val btnVirtualAccount = view.findViewById<ConstraintLayout>(R.id.btn_menu_virtual_account)
        btnVirtualAccount.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(R.id.action_homeFragment_to_transferVirtualAccountHome)
            }
        }

        val btnEWallet = view.findViewById<ConstraintLayout>(R.id.btn_menu_ewallet)
        btnEWallet.setOnClickListener {
            Snackbar.make(requireView(), "Fitur belum tersedia", Snackbar.LENGTH_SHORT).show()
        }

        val btnTransferAntarBank = view.findViewById<ConstraintLayout>(R.id.btn_menu_transfer_antarbank)
        btnTransferAntarBank.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transferAntarBankHome)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_qrisScanQRFragment)
        }


        setUpBottomNavBar()
        getInfoSaldo()
        visibilitySaldo()
        clipBoardCardNumber()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle language not supported
                Log.e("TTS", "Indonesian language is not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun getInfoSaldo() {
        homeViewModel.getSaldo()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.saldoUiState.collectLatest { uiState ->
                    if (uiState.isLoading) {
                        showLoadingInfoSaldo()
                    } else {
                        hideLoadingInfoSaldo()
                        uiState.data?.let { saldo ->
                            binding.tvTopbgAccountName.text = saldo.username
                            binding.cardNasabahInfo.tvNamaNasabah.text = saldo.username
                            binding.cardNasabahInfo.tvNamaNasabah.contentDescription =
                                getString(R.string.account_name_content_confirmation)
                            binding.cardNasabahInfo.tvNoRekeningCard.text = saldo.accountNumber
                            binding.cardNasabahInfo.tvNoRekeningCard.contentDescription =
                                getString(R.string.account_number_content_confirmation)
                            setupAccessibility()
                            if (homeViewModel.isSaldoVisible.value == true) {
                                binding.cardNasabahInfo.tvSaldoRekeningCard.text =
                                    StringBuilder().append("Rp ")
                                        .append(CurrencyFormatter.formatCurrency(saldo.amount))
                            } else {
                                binding.cardNasabahInfo.tvSaldoRekeningCard.text =
                                    getString(R.string.tv_saldo_card_rekening_home)
                            }
                        } ?: run {
                            showLoadingInfoSaldo()
                        }
                        uiState.message?.let {message->
                            Log.d("HomeFragment", message)
                        }
                    }
                }

            }

        }
    }

    private fun clipBoardCardNumber(){
        binding.cardNasabahInfo.btnNorekCopy.setOnClickListener {
            val cardNumber = binding.cardNasabahInfo.tvNoRekeningCard.text.toString()
            val cardNumberLabel = binding.cardNasabahInfo.tvNamaNasabah.text.toString()

            requireContext().copyToClipboard(
                getString(
                    R.string.copy_to_clipboard,
                    cardNumberLabel,
                    cardNumber
                ))
            Snackbar.make(requireView(),
                getString(R.string.succes_clipboard_card_number), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showLoadingInfoSaldo() {
        binding.cardNasabahInfo.pbNoRekeningCard.visibility = View.VISIBLE
        binding.cardNasabahInfo.pbNamaNasabah.visibility = View.VISIBLE
        binding.cardNasabahInfo.pbSaldoRekeningCard.visibility = View.VISIBLE
        binding.progressBarTopName.visibility = View.VISIBLE

        binding.cardNasabahInfo.tvSaldoRekeningCard.visibility = View.GONE
        binding.cardNasabahInfo.btnNorekCopy.visibility = View.GONE
        binding.cardNasabahInfo.btnSaldoVisibility.visibility = View.GONE
        binding.cardNasabahInfo.tvSaldoRekeningCard.visibility = View.GONE
    }


    private fun hideLoadingInfoSaldo() {
        binding.cardNasabahInfo.pbNoRekeningCard.visibility = View.GONE
        binding.cardNasabahInfo.pbNamaNasabah.visibility = View.GONE
        binding.cardNasabahInfo.pbSaldoRekeningCard.visibility = View.GONE
        binding.progressBarTopName.visibility = View.GONE

        binding.cardNasabahInfo.tvSaldoRekeningCard.visibility = View.VISIBLE
        binding.cardNasabahInfo.btnNorekCopy.visibility = View.VISIBLE
        binding.cardNasabahInfo.btnSaldoVisibility.visibility = View.VISIBLE
        binding.cardNasabahInfo.tvSaldoRekeningCard.visibility = View.VISIBLE
    }

    private fun visibilitySaldo() {
        binding.cardNasabahInfo.btnSaldoVisibility.setOnClickListener {
            homeViewModel.toggleSaldoVisibility()
        }

        homeViewModel.isSaldoVisible.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) {
                homeViewModel.saldoUiState.value.data?.let {
                    val balanceText = StringBuilder().append("Rp ")
                        .append(CurrencyFormatter.formatCurrency(it.amount)).toString()
                    binding.cardNasabahInfo.tvSaldoRekeningCard.text = balanceText
                    binding.cardNasabahInfo.tvSaldoRekeningCard.contentDescription =
                        getString(R.string.balance_description, balanceText)
                    binding.cardNasabahInfo.tvSaldoRekeningCard.isClickable = false
                }
                binding.cardNasabahInfo.btnSaldoVisibility.setImageResource(R.drawable.ic_rekening_no_visibility)
                binding.cardNasabahInfo.btnSaldoVisibility.contentDescription = getString(R.string.tv_talkback_sembunyikan_saldo)
            } else {
                binding.cardNasabahInfo.tvSaldoRekeningCard.text =
                    getString(R.string.tv_saldo_card_rekening_home)
                if (isTalkBackEnabled()){
                    binding.cardNasabahInfo.tvSaldoRekeningCard.contentDescription =
                        getString(R.string.saldo_disembunyikan)
                }
                binding.cardNasabahInfo.tvSaldoRekeningCard.isClickable = false
                binding.cardNasabahInfo.btnSaldoVisibility.setImageResource(R.drawable.ic_rekening_visibility)
                binding.cardNasabahInfo.btnSaldoVisibility.contentDescription = getString(R.string.tv_talkback_lihat_saldo)
            }
        }

        binding.cardNasabahInfo.tvSaldoRekeningCard.setOnClickListener {
            speakBalance()
        }
    }

    private fun isTalkBackEnabled(): Boolean {
        return accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled
    }

    private fun setupAccessibility() {
        val helloTextView = binding.tvTopbgHelloPlaceholder
        val nameTextView = binding.tvTopbgAccountName

        val accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                val greeting = "${helloTextView.text}, ${nameTextView.text}"
                info.contentDescription = greeting
            }
        }

        helloTextView.accessibilityDelegate = accessibilityDelegate
        nameTextView.accessibilityDelegate = accessibilityDelegate
    }

    private fun speakBalance() {
        val textToSpeak = binding.cardNasabahInfo.tvSaldoRekeningCard.contentDescription.toString()
        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun speakAccountName() {
        if (isTalkBackEnabled()) {
            val accountName = binding.cardNasabahInfo.tvNamaNasabah.text.toString()
            val prefix = getString(R.string.account_name_prefix)
            val textToSpeak = "$prefix $accountName"
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    private fun speakAccountNumber() {
        if (isTalkBackEnabled()) {
            val accountNumber = binding.cardNasabahInfo.tvNoRekeningCard.text.toString()
            val prefix = getString(R.string.account_number_prefix)
            val digitsSpaced = accountNumber.replace("".toRegex(), " ")
            val textToSpeak = "$prefix $digitsSpaced"
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    private fun setUpBottomNavBar() {
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(0).isChecked = true
        binding.bottomNavigationView.menu.getItem(1).isCheckable = false
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        binding.bottomNavigationView.menu.getItem(3).isCheckable = false
        binding.bottomNavigationView.menu.getItem(4).isCheckable = false

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_navbar_beranda -> {
                    true
                }

                R.id.menu_navbar_mutasi -> {
                    findNavController().navigate(R.id.action_homeFragment_to_mutasiFragment)
                    false
                }

                R.id.menu_navbar_qris -> {

                    false
                }

                R.id.menu_navbar_favorit -> {
                    false
                }

                R.id.menu_navbar_akun -> {
                    false
                }

                else -> {
                    false
                }
            }
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }


}