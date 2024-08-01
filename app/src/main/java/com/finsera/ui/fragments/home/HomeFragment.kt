package com.finsera.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.finsera.R
import com.finsera.common.utils.Resource
import com.finsera.databinding.FragmentHomeBinding
import com.finsera.databinding.FragmentLoginBinding
import com.finsera.ui.fragments.home.viewmodel.HomeViewModel
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.shape.MaterialShapeDrawable
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.finsera.common.utils.format.CurrencyFormatter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()

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

        setUpBottomNavBar()

        val btnInfoSaldo = view.findViewById<ConstraintLayout>(R.id.btn_menu_infosaldo)
        btnInfoSaldo?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_infoSaldoFragment)
        }

        getInfoSaldo()
        visibilitySaldo()
    }

    private fun getInfoSaldo() {
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
                            binding.cardNasabahInfo.tvNoRekeningCard.text = saldo.accountNumber
                            if (homeViewModel.isSaldoVisible.value == true) {
                                binding.cardNasabahInfo.tvSaldoRekeningCard.text =
                                    StringBuilder().append("Rp ")
                                        .append(CurrencyFormatter.formatCurrency(saldo.amount))
                            } else {
                                binding.cardNasabahInfo.tvSaldoRekeningCard.text =
                                    getString(R.string.tv_saldo_card_rekening_home)
                            }
                        } ?: run {
                            binding.tvTopbgAccountName.text =
                                getString(R.string.tv_topbg_account_name)
                            binding.cardNasabahInfo.tvNamaNasabah.text =
                                getString(R.string.tv_nama_nasabah_placeholder)
                            binding.cardNasabahInfo.tvNoRekeningCard.text =
                                getString(R.string.tv_rekening_placeholder)
                            binding.cardNasabahInfo.tvSaldoRekeningCard.text =
                                getString(R.string.tv_saldo_card_rekening_home)
                        }
                        uiState.message?.let {message->
                            Log.d("HomeFragment", message)
                        }
                    }
                }

            }

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
                    binding.cardNasabahInfo.tvSaldoRekeningCard.text = StringBuilder().append("Rp ")
                        .append(CurrencyFormatter.formatCurrency(it.amount))
                }
                binding.cardNasabahInfo.btnSaldoVisibility.setImageResource(R.drawable.ic_rekening_no_visibility)
            } else {
                binding.cardNasabahInfo.tvSaldoRekeningCard.text =
                    getString(R.string.tv_saldo_card_rekening_home)
                binding.cardNasabahInfo.btnSaldoVisibility.setImageResource(R.drawable.ic_rekening_visibility)
            }
        }

        val btnEWallet = view?.findViewById<ConstraintLayout>(R.id.btn_menu_ewallet)
        btnEWallet?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transferEWalletHomeFragment)
        }
    }

    private fun setUpBottomNavBar() {
        // set background for bottomNavigationView to null
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


}