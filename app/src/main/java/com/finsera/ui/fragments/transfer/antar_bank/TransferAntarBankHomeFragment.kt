package com.finsera.ui.fragments.transfer.antar_bank

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.finsera.R
import com.finsera.databinding.FragmentTransferAntarBankHomeBinding
import com.finsera.databinding.FragmentTransferVirtualAccountHomeBinding

class TransferAntarBankHomeFragment : Fragment() {
    private var _binding: FragmentTransferAntarBankHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransferAntarBankHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnInput.setOnClickListener {
            findNavController().navigate(R.id.action_transferAntarBankHome_to_transferAntarBankForm)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//    companion object {
//        fun newInstance() = TransferAntarBankHomeFragment()
//    }
//
//    private val viewModel: TransferAntarBankHomeViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // TODO: Use the ViewModel
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_transfer_antar_bank_home, container, false)
//    }
//}