package com.finsera.common.utils.format

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatCurrency(value: Double): String {
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return format.format(value)
    }
}