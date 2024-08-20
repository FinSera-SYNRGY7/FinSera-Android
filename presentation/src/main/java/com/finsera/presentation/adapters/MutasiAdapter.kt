package com.finsera.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finsera.domain.model.Mutasi
import com.finsera.presentation.R
import com.finsera.presentation.databinding.MutasiItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MutasiAdapter() : ListAdapter<Mutasi, MutasiAdapter.MutasiViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MutasiViewHolder {
        val binding = MutasiItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MutasiViewHolder(binding)
    }

    class MutasiViewHolder(private val binding: MutasiItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Mutasi) {
            binding.tvNominalUang.text = amountConverter(data)
            binding.tvDate.text = formatDateString(data.transactionDate)
            val formattedTime = formatTimeString(data.transactionDate)
            binding.tvWaktuTransaksi.text = formattedTime
            binding.tvDetailMutasi.text = mutasiDescription(data)
            binding.tvNamaBank.text = data.destinationBankName

            binding.tvWaktuTransaksi.contentDescription = formatTimeForAccessibility(formattedTime)

            when (data.transactionInformation) {
                "UANG_MASUK" -> {
                    binding.tvNominalUang.setTextColor(
                        binding.root.context.getColor(R.color.secondary_green))

                    Glide
                        .with(binding.root.context)
                        .load(R.drawable.ic_uangmasuk_mutasi)
                        .into(binding.icStatusUang)
                }
                "UANG_KELUAR" -> {
                    binding.tvNominalUang.setTextColor(
                        binding.root.context.getColor(R.color.secondary_red)
                    )

                    Glide
                        .with(binding.root.context)
                        .load(R.drawable.ic_uangkeluar_mutasi)
                        .into(binding.icStatusUang)
                }
                else -> binding.tvNominalUang.setTextColor(Color.BLACK)
            }
        }

        private fun formatTimeString(dateString: String): String {
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            val outputFormatter = SimpleDateFormat("HH:mm:ss 'WIB'", Locale.getDefault())

            val date = inputFormatter.parse(dateString)
            return outputFormatter.format(date!!)
        }

        private fun formatTimeForAccessibility(timeString: String): String {
            val parts = timeString.split(":")
            return "Jam ${parts[0]} ${parts[1]} W I B"
        }

        private fun formatDateString(dateString: String): String {
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            val outputFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

            val date = inputFormatter.parse(dateString)
            return outputFormatter.format(date!!)
        }

        private fun amountConverter(data: Mutasi) : String {
            val finalAmount = when(data.transactionInformation) {
                "UANG_MASUK" -> "+ Rp ${data.amount.toInt()}"
                "UANG_KELUAR" -> "- Rp ${data.amount.toInt()}"
                else -> "Rp${data.amount.toInt()}"
            }
//            Log.d("MutasiAdapter", "Converted amount: $finalAmount, Transaction Type = ${data.transactionInformation}")
            return finalAmount
        }

        private fun mutasiDescription(data: Mutasi) : String {
            val finalDesc = when(data.transactionInformation) {
                "UANG_MASUK" -> "Terima Uang dari ${data.destinationName}"
                "UANG_KELUAR" -> "Transfer Uang ke ${data.destinationName}"
                else -> "${data.destinationName}"
            }
            return finalDesc
        }
    }

    override fun onBindViewHolder(holder: MutasiViewHolder, position: Int) {
        val data = getItem(position)

        holder.bind(data)
    }

    companion object{
        val DIFF_CALLBACK=object : DiffUtil.ItemCallback<Mutasi>(){
            override fun areItemsTheSame(oldItem: Mutasi, newItem: Mutasi): Boolean {
                return oldItem==newItem
            }
            override fun areContentsTheSame(oldItem: Mutasi, newItem: Mutasi): Boolean {
                return oldItem==newItem
            }
        }
    }
}

