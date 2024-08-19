package com.finsera.presentation.fragments.transfer.sesama_bank

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.finsera.common.utils.Constant
import com.finsera.common.utils.permission.HandlePermission.openAppPermissionSettings
import com.finsera.domain.model.TransferSesama
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentTransferSesamaBankSuksesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class TransferSesamaBankSuksesFragment : Fragment() {
    private var _binding: FragmentTransferSesamaBankSuksesBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransferSesamaBankSuksesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val namaPengirim = arguments?.getString(Constant.NAMA_NASABAH)
        val rekeningPengirim = arguments?.getString(Constant.NOMOR_REKENING_NASABAH)

        val dataTransferBerhasil = arguments?.getParcelable<TransferSesama>(Constant.TRANSFER_SESAMA_BERHASIL_BUNDLE)?.let {
            binding.cardTransaksiBerhasil.tvDate.text = it.transactionDate
            binding.cardTransaksiBerhasil.tvNominal.text = it.nominal
            binding.cardTransaksiBerhasil.tvBiayaAdmin.text = "Gratis"
            binding.cardTransaksiBerhasil.tvNomorTransaksi.text = it.transactionNum

            binding.cardTransaksiBerhasil.tvBankTujuan.text = "Bank BCA"
            binding.cardTransaksiBerhasil.tvNamaPenerima.text = it.nameRecipient
            binding.cardTransaksiBerhasil.tvRekeningTujuan.text = it.accountnumRecipient
            binding.cardTransaksiBerhasil.tvCatatan.text = it.note

            binding.cardTransaksiBerhasil.tvNamaPengirim.text = namaPengirim
            binding.cardTransaksiBerhasil.tvRekeningPengirim.text = rekeningPengirim

            binding.cardTransaksiBerhasilScreenshot.tvDate.text = it.transactionDate
            binding.cardTransaksiBerhasilScreenshot.tvNominal.text = it.nominal
            binding.cardTransaksiBerhasilScreenshot.tvBiayaAdmin.text = "Gratis"
            binding.cardTransaksiBerhasilScreenshot.tvNomorTransaksi.text = it.transactionNum

            binding.cardTransaksiBerhasilScreenshot.tvBankTujuan.text = "Bank BCA"
            binding.cardTransaksiBerhasilScreenshot.tvNamaPenerima.text = it.nameRecipient
            binding.cardTransaksiBerhasilScreenshot.tvRekeningTujuan.text = it.accountnumRecipient
            binding.cardTransaksiBerhasilScreenshot.tvCatatan.text = it.note

            binding.cardTransaksiBerhasilScreenshot.tvNamaPengirim.text = namaPengirim
            binding.cardTransaksiBerhasilScreenshot.tvRekeningPengirim.text = rekeningPengirim
        }

        binding.cardTransaksiBerhasil.btnBackToMenu.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        val captureButton = binding.cardTransaksiBerhasil.btnDownload
        captureButton.setOnClickListener {
            safeSaveToGallery()
        }

        val shareButton = binding.cardTransaksiBerhasil.btnShare
        shareButton.setOnClickListener {
            safeShareImageTo()
        }

        setAccessibilityDescriptions()
    }

    fun getBitmapFromUiView(context: Context, myView: View) : Bitmap {
        val bitmap = Bitmap.createBitmap(
            myView.width,
            myView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        myView.draw(canvas)

        return bitmap
    }

    /**Save Bitmap To Gallery
     * @param bitmap The bitmap to be saved in Storage/Gallery*/
    private fun saveBitmapImage(bitmap: Bitmap) : Uri? {
        val timestamp = System.currentTimeMillis()

        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        var uri : Uri? = null
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            uri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                try {
                    val outputStream = requireActivity().contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "saveBitmapImage: ", e)
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    requireActivity().contentResolver.update(uri, values, null, null)

                    Snackbar.make(requireView(), "Bukti Transaksi berhasil disimpan di Galeri.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
            }
        } else {
            val imageFileFolder = File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name))
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                uri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                Snackbar.make(requireView(), "Bukti Transaksi berhasil disimpan di Galeri.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "saveBitmapImage: ", e)
            }
        }

        normalMode()
        return uri
    }

    private fun formatDigitNumberAccessibility(digitNumberTalkback: String): String {
        return digitNumberTalkback.map { it.toString() }.joinToString(" ")
    }

    private fun setAccessibilityDescriptions() {
        binding.cardTransaksiBerhasil.apply {
            val formattedDigitNumberBankRecipient = formatDigitNumberAccessibility(tvRekeningTujuan.text.toString())
            val formattedDigitNumberBankAccount = formatDigitNumberAccessibility(tvRekeningPengirim.text.toString())
            val formattedDigitNumberTransaction = formatDigitNumberAccessibility(tvNomorTransaksi.text.toString())
            tvDate.contentDescription = getString(R.string.tanggal_transaksi_desc, tvDate.text)
            tvNominal.contentDescription = getString(R.string.nominal_transfer_desc, tvNominal.text)
            tvBiayaAdmin.contentDescription = getString(R.string.biaya_admin_desc, tvBiayaAdmin.text)
            tvNomorTransaksi.contentDescription = getString(R.string.nomor_transaksi_desc, formattedDigitNumberTransaction)
            tvNamaPengirim.contentDescription = getString(R.string.nama_pengirim_desc, tvNamaPengirim.text)
            tvRekeningPengirim.contentDescription = getString(R.string.rekening_pengirim_desc, formattedDigitNumberBankAccount)
            tvBankTujuan.contentDescription = getString(R.string.bank_tujuan_desc, tvBankTujuan.text)
            tvNamaPenerima.contentDescription = getString(R.string.nama_penerima_desc, tvNamaPenerima.text)
            tvRekeningTujuan.contentDescription = getString(R.string.rekening_tujuan_desc, formattedDigitNumberBankRecipient)
            tvCatatan.contentDescription = getString(R.string.catatan_desc, tvCatatan.text)
        }
    }

    private fun saveToGallery() {
        saveToGalleryMode()
        val bitmap = getBitmapFromUiView(requireActivity(), binding.transferSesamaBankBerhasil)
        imageUri = saveBitmapImage(bitmap)
    }

    private fun shareImageTo() {
        safeSaveToGallery()
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            // Example: content://com.google.android.apps.photos.contentprovider/...
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun saveToGalleryMode() {
        binding.transferSesamaBankBerhasil.apply {
            binding.cardTransaksiBerhasil.root.visibility = View.INVISIBLE
            binding.cardTransaksiBerhasilScreenshot.root.visibility = View.VISIBLE
        }
    }

    private fun normalMode() {
        binding.transferSesamaBankBerhasil.apply {
            binding.cardTransaksiBerhasil.root.visibility = View.VISIBLE
            binding.cardTransaksiBerhasilScreenshot.root.visibility = View.INVISIBLE
        }
    }

    private fun safeSaveToGallery() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                saveToGallery()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                permissionStorageDialog()
            }

            else -> {
                // Request the permission
                requestPermissionSafeSaveToGallery.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun safeShareImageTo() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                shareImageTo()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                permissionStorageDialog()
            }

            else -> {
                // Request the permission
                requestPermissionSafeShareImageTo.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestPermissionSafeSaveToGallery = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            saveToGallery()
        } else {
            permissionStorageDialog()
        }
    }

    private val requestPermissionSafeShareImageTo = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            shareImageTo()
        } else {
            // Explain to the user that the feature is unavailable because the
            // features require a permission that the user has denied.
            permissionStorageDialog()
        }
    }

    private fun permissionStorageDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Izin Aplikasi FinSera")
            .setMessage(resources.getString(R.string.izin_aplikasi_finsera_desc))
            .setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss()
                Snackbar.make(requireView(), "Fitur tidak dapat dijalankan karena izin penyimpanan file pada aplikasi FinSera tidak diizinkan", Snackbar.LENGTH_SHORT).show()
            }
            .setPositiveButton("Ya") { dialog, which ->
                requireActivity().openAppPermissionSettings()
            }
            .show()
    }

}