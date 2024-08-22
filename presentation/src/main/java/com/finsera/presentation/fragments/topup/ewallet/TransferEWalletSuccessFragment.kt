package com.finsera.presentation.fragments.topup.ewallet

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
import com.finsera.presentation.R
import com.finsera.presentation.databinding.FragmentTransferEWalletSuccessBinding
import com.finsera.presentation.fragments.topup.ewallet.bundle.SuccessEWalletBundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class TransferEWalletSuccessFragment : Fragment() {

    private var _binding: FragmentTransferEWalletSuccessBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransferEWalletSuccessBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments?.getParcelable<SuccessEWalletBundle>(Constant.DATA_TF_EWALLET_BUNDLE)
        if (bundle != null) {
            setupData(bundle)
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

    private fun setupData(bundle: SuccessEWalletBundle) {
        binding.cardTransaksiBerhasil.tvDate.text = bundle.transactionDate
        binding.cardTransaksiBerhasilScreenshot.tvDate.text = bundle.transactionDate
        binding.cardTransaksiBerhasil.tvNominal.text = bundle.nominal
        binding.cardTransaksiBerhasilScreenshot.tvNominal.text = bundle.nominal
        if (bundle.feeAdmin.isNotEmpty()) {
            binding.cardTransaksiBerhasil.tvBiayaAdmin.text = bundle.feeAdmin
            binding.cardTransaksiBerhasilScreenshot.tvBiayaAdmin.text = bundle.feeAdmin
        } else {
            binding.cardTransaksiBerhasil.tvBiayaAdmin.text = getString(R.string.free)
            binding.cardTransaksiBerhasilScreenshot.tvBiayaAdmin.text = getString(R.string.free)
        }
        binding.cardTransaksiBerhasil.tvNomorTransaksi.text = bundle.transactionNum
        binding.cardTransaksiBerhasilScreenshot.tvNomorTransaksi.text = bundle.transactionNum
        binding.cardTransaksiBerhasil.tvNamaPengirim.text = bundle.nameSender
        binding.cardTransaksiBerhasilScreenshot.tvNamaPengirim.text = bundle.nameSender
        binding.cardTransaksiBerhasil.tvRekeningPengirim.text = bundle.accountSender
        binding.cardTransaksiBerhasilScreenshot.tvRekeningPengirim.text = bundle.accountSender
        binding.cardTransaksiBerhasil.tvNamaPenerima.text = bundle.ewalletAccountName
        binding.cardTransaksiBerhasilScreenshot.tvNamaPenerima.text = bundle.ewalletAccountName
        binding.cardTransaksiBerhasil.tvRekeningTujuanLabel.text =
            StringBuilder().append("Nomor ").append(bundle.ewalletName)
        binding.cardTransaksiBerhasilScreenshot.tvRekeningTujuanLabel.text =
            StringBuilder().append("Nomor ").append(bundle.ewalletName)
        binding.cardTransaksiBerhasil.tvRekeningTujuan.text = bundle.ewalletAccount
        binding.cardTransaksiBerhasilScreenshot.tvRekeningTujuan.text = bundle.ewalletAccount
        binding.cardTransaksiBerhasil.tvBankLabel.text =
            StringBuilder().append("E-Wallet").append(" Tujuan")
        binding.cardTransaksiBerhasilScreenshot.tvBankLabel.text =
            StringBuilder().append("E-Wallet").append(" Tujuan")
        binding.cardTransaksiBerhasil.tvBankTujuan.text = bundle.ewalletName
        binding.cardTransaksiBerhasilScreenshot.tvBankTujuan.text = bundle.ewalletName


        binding.cardTransaksiBerhasil.rlCatatan.visibility = View.GONE
        binding.cardTransaksiBerhasilScreenshot.rlCatatan.visibility = View.GONE
    }

    private fun formatDigitNumberAccessibility(digitNumberTalkback: String): String {
        return digitNumberTalkback.map { it.toString() }.joinToString(" ")
    }

    private fun setAccessibilityDescriptions() {
        binding.cardTransaksiBerhasil.apply {
            val formattedDigitNumberBankRecipient = formatDigitNumberAccessibility(tvRekeningTujuan.text.toString())
            val formattedDigitNumberBankAccount = formatDigitNumberAccessibility(tvRekeningPengirim.text.toString())
            val formattedDigitNumberTransaction = formatDigitNumberAccessibility(tvNomorTransaksi.text.toString())
            rlTanggal.contentDescription = getString(R.string.tanggal_transaksi_desc, tvDate.text)
            rlNominal.contentDescription = getString(R.string.nominal_transfer_desc, tvNominal.text)
            rlBiayaAdmin.contentDescription = getString(R.string.biaya_admin_desc, tvBiayaAdmin.text)
            rlNoTransaksi.contentDescription = getString(R.string.nomor_transaksi_desc, formattedDigitNumberTransaction)
            rlNamaPengirim.contentDescription = getString(R.string.nama_pengirim_desc, tvNamaPengirim.text)
            rlRekeningPengirim.contentDescription = getString(R.string.rekening_pengirim_desc, formattedDigitNumberBankAccount)
            rlBankTujuan.contentDescription = getString(R.string.e_wallet_tujuan_desc, tvBankTujuan.text)
            rlNamaPenerima.contentDescription = getString(R.string.nama_penerima_desc, tvNamaPenerima.text)
            rlRekeningTujuan.contentDescription = getString(R.string.nomor_e_wallet_desc, formattedDigitNumberBankRecipient)
        }
    }

    private fun saveToGalleryMode() {
        binding.transferEWalletSuccess.apply {
            binding.cardTransaksiBerhasil.root.visibility = View.INVISIBLE
            binding.cardTransaksiBerhasilScreenshot.root.visibility = View.VISIBLE
        }
    }

    private fun normalMode() {
        binding.transferEWalletSuccess.apply {
            binding.cardTransaksiBerhasil.root.visibility = View.VISIBLE
            binding.cardTransaksiBerhasilScreenshot.root.visibility = View.INVISIBLE
        }
    }

    private fun saveToGallery() {
        saveToGalleryMode()
        val bitmap = getBitmapFromUiView(requireActivity(), binding.transferEWalletSuccess)
        imageUri = saveBitmapImage(bitmap)
    }

    private fun getBitmapFromUiView(context: Context, myView: View): Bitmap {
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


    private fun saveBitmapImage(bitmap: Bitmap): Uri? {
        val timestamp = System.currentTimeMillis()

        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        var uri: Uri? = null
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/" + getString(R.string.app_name)
            )
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            uri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
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

                    Snackbar.make(
                        requireView(),
                        getString(R.string.success_transfer_save_to_galery), Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
            }
        } else {
            val imageFileFolder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + '/' + getString(R.string.app_name)
            )
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
                uri = requireActivity().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )

                Snackbar.make(
                    requireView(),
                    getString(R.string.success_transfer_label), Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e(TAG, "saveBitmapImage: ", e)
            }
        }

        normalMode()
        return uri
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

    private fun safeShareImageTo() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                shareImageTo()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                permissionStorageDialog()
            }

            else -> {
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
            permissionStorageDialog()
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


    private fun permissionStorageDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Izin Aplikasi FinSera")
            .setMessage(resources.getString(R.string.izin_penyimpanan_aplikasi_finsera_desc))
            .setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss()
                Snackbar.make(
                    requireView(),
                    "Fitur tidak dapat dijalankan karena izin penyimpanan file pada aplikasi FinSera tidak diizinkan",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setPositiveButton("Ya") { dialog, which ->
                requireActivity().openAppPermissionSettings()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


}