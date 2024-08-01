package com.finsera.ui.fragments.info.mutasi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finsera.R
import com.finsera.common.utils.dialog.DatePickerFragment
import com.finsera.databinding.FragmentMutasiBinding

class MutasiFragment : Fragment(), DatePickerFragment.DialogDateListener{

    private var _binding: FragmentMutasiBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMutasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFilter.setOnClickListener {
            showFilter()
        }

        binding.btnHistory.setOnClickListener {
            showHistory()
        }

        binding.viewFilter.clStartDate.setOnClickListener {
            showDatePicker("starDatePicker")
        }

        binding.viewFilter.cleEndDate.setOnClickListener {
            showDatePicker("endDatePicker")
        }

        binding.viewFilter.btnNext.setOnClickListener {
            showResultOfFilter()
        }
    }

    private fun showResultOfFilter() {
        binding.rvMutasi.visibility = View.VISIBLE
        binding.btnDownload.visibility = View.VISIBLE
        binding.viewFilter.clMutasiFilter.visibility = View.GONE
    }

    private fun showDatePicker(tag: String?) {
        val newFragment = DatePickerFragment()
        newFragment.setDialogDateListener(this)
        newFragment.show(parentFragmentManager, tag)
    }

    private fun showHistory() {

        binding.viewFilter.clMutasiFilter.visibility = View.GONE
        binding.rvMutasi.visibility = View.VISIBLE
        binding.btnDownload.visibility = View.VISIBLE

        //set color
        binding.btnFilter.setBackgroundColor(resources.getColor(R.color.neutral_03))
        binding.btnHistory.setBackgroundColor(resources.getColor(R.color.primary_blue))
    }

    private fun showFilter() {
        binding.viewFilter.clMutasiFilter.visibility = View.VISIBLE
        binding.rvMutasi.visibility = View.GONE
        binding.btnDownload.visibility = View.GONE

        //set color
        binding.btnFilter.setBackgroundColor(resources.getColor(R.color.primary_blue))
        binding.btnHistory.setBackgroundColor(resources.getColor(R.color.neutral_03))
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, day: Int) {
        if (tag.equals("starDatePicker")) {
            binding.viewFilter.tvStartDateValue.text = "$day-${month + 1}-$year"
        } else {
            binding.viewFilter.tvEndDateValue.text = "$day-${month + 1}-$year"
        }
    }

}
