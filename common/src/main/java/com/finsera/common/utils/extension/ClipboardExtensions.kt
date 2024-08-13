package com.finsera.common.utils.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}