package com.finsera.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.finsera.R
import com.finsera.databinding.FragmentLoginBinding
import com.finsera.ui.fragments.auth.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel : LoginViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLoggedInStatus()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.editText?.text.toString()
            val password = binding.etPassword.editText?.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.userLogin(username, password)
            } else {
                Snackbar.make(requireView(), "Mohon untuk mengisi kolom Username dan Password sebelum melanjutkan.", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginScreenUIState.collectLatest { uiState ->
                    uiState.message?.let { message ->
                        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                        loginViewModel.userMessageShown()
                    }

                    if(uiState.isUserLoggedIn) {
                        if (findNavController().currentDestination?.id == R.id.loginFragment) {
                            findNavController().navigate(R.id.action_loginFragment_to_loginPinFragment)
                        }
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

    private fun observeLoggedInStatus() {
        loginViewModel.userLoggedInStatus.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().navigate(R.id.action_loginFragment_to_loginPinFragment)
            }
        }
    }
}