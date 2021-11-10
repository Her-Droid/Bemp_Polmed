package com.admin.bempadmin.view.activity

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityTambahPrestasiBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.helper.PermissionHelper
import com.admin.bempadmin.helper.TB_PRESTASI
import com.admin.bempadmin.helper.UPDATE
import com.admin.bempadmin.model.Anggota
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.Prestasi
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.PrestasiViewModel
import com.bumptech.glide.Glide
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception

class TambahPrestasiActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTambahPrestasiBinding
    private lateinit var prestasiViewModel: PrestasiViewModel
    private lateinit var file: File
    private var uri: Uri = Uri.EMPTY

    private var idUpdate = ""
    private var imageUrlUpdate = ""
    private var update = "ADD"

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                this.uri = uri
                file = File(getPath(uri))
                val bitmap: Bitmap = if (android.os.Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri));
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }

                getResizedBitmap(bitmap)?.let { it1 -> rotateImage(it1) }?.let { it2 ->
                    setToImageView(
                        it2
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahPrestasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.includeToolbar.imgBtnBack -> {
                finish()
            }
            binding.btnSimpan -> {
                if (update == UPDATE) {
                    updatePrestasi()
                } else {
                    savePrestasi()
                }
            }
            binding.imgFoto -> {
                startForResult.launch("image/*")
            }
        }
    }

    private fun setView() {
        PermissionHelper().PermissionHelper(this@TambahPrestasiActivity)
        prestasiViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@TambahPrestasiActivity)
        ).get(PrestasiViewModel::class.java)

        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnSimpan.setOnClickListener(this)
        binding.imgFoto.setOnClickListener(this)

        update = intent.getStringExtra(UPDATE).toString()
        if (update == UPDATE) {
            val prestasiUpdate = intent.getParcelableExtra<Prestasi>(TB_PRESTASI)
            if (prestasiUpdate != null) {
                idUpdate = prestasiUpdate.id
                imageUrlUpdate = prestasiUpdate.imageUrl
                binding.edtNim.setText(prestasiUpdate.nim)
                binding.edtNama.setText(prestasiUpdate.nama)
                binding.edtPrestasi.setText(prestasiUpdate.prestasi)
                Glide.with(this@TambahPrestasiActivity)
                    .load(imageUrlUpdate)
                    .into(binding.imgFoto)
            }
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.update_prestasi)
        } else {
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.tambah_prestasi)
        }
    }

    private fun getPath(uri: Uri): String {
        val schema: String = uri.scheme.toString()
        if ("http" == schema || "https" == schema) {
            return uri.toString()
        } else if (ContentResolver.SCHEME_CONTENT == schema) {
            val projection = arrayOf(MediaStore.MediaColumns._ID)
            var cursor: Cursor? = null
            var filePath = ""
            try {
                cursor = contentResolver.query(uri, projection, null, null, null)
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(0)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                // do nothing
            } finally {
                try {
                    cursor?.close()
                } catch (e2: Exception) {
                    // do nothing
                }
            }
            if (TextUtils.isEmpty(filePath)) {
                try {
                    val contentResolver: ContentResolver = contentResolver
                    val selection = MediaStore.Images.Media._ID + "= ?"
                    var id: String = uri.lastPathSegment.toString()
                    if (!TextUtils.isEmpty(id) && id.contains(":")) {
                        id = id.split(":").toTypedArray()[1]
                    }
                    val selectionArgs = arrayOf<String?>(id)
                    cursor = contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        filePath = cursor.getString(0)
                        cursor.close()
                    }
                } catch (e: Exception) {
                    // do nothing
                } finally {
                    try {
                        cursor?.close()
                    } catch (e: Exception) {
                        // do nothing
                    }
                }
            }
            return filePath
        }
        return ""
    }

    private fun rotateImage(bitmap: Bitmap): Bitmap? {
        var exifInterface: ExifInterface? = null
        try {
            exifInterface = ExifInterface(file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var orientation = 0
        if (exifInterface != null) {
            orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
        }
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(
                0f
            )
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            else -> {
            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getResizedBitmap(image: Bitmap): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = 1200
            height = (width / bitmapRatio).toInt()
        } else {
            height = 1200
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun setToImageView(bmp: Bitmap) {
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes)
        val bitmapKtp = BitmapFactory.decodeStream(ByteArrayInputStream(bytes.toByteArray()))
        Glide.with(this@TambahPrestasiActivity)
            .load(bitmapKtp)
            .into(binding.imgFoto)
    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    private fun savePrestasi() {
        val nim = binding.edtNim.text.toString().trim()
        val nama = binding.edtNama.text.toString().trim()
        val prestasi = binding.edtPrestasi.text.toString().trim()

        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahPrestasiActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahPrestasiActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        when {
            nim.isEmpty() -> {
                binding.edtNim.error = resources.getString(R.string.nimerr)
                binding.edtNim.requestFocus()
            }
            nama.isEmpty() -> {
                binding.edtNama.error = resources.getString(R.string.namamhserr)
                binding.edtNama.requestFocus()
            }
            prestasi.isEmpty() -> {
                binding.edtPrestasi.error = resources.getString(R.string.prestasierr)
                binding.edtPrestasi.requestFocus()
            }
            uri.toString().isEmpty() -> {
                alertDialog.show()

                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.fotokosong)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            else -> {
                binding.includeProgressBar.root.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.GONE

                prestasiViewModel.getMaxId().observe(this) {
                    prestasiViewModel.prestasi = Prestasi(nama = nama, nim = nim)
                    prestasiViewModel.foto = Foto(
                        System.currentTimeMillis().toString(),
                        getFileExtension(uri).toString(),
                        uri
                    )
                    prestasiViewModel.uploadFoto().observe(this) { imageUrl ->
                        if (imageUrl != "0") {
                            prestasiViewModel.prestasi =
                                Prestasi((it + 1).toString(), nim, nama, prestasi, imageUrl)
                            simpanUpdatePrestasi()
                        } else {
                            alertDialog.show()
                            popupMessageAdminBinding.tvMessage.text =
                                resources.getString(R.string.gagalanggota)
                            popupMessageAdminBinding.btnOke.setOnClickListener {
                                alertDialog.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updatePrestasi() {
        val nim = binding.edtNim.text.toString().trim()
        val nama = binding.edtNama.text.toString().trim()
        val prestasi = binding.edtPrestasi.text.toString().trim()
        when {
            nim.isEmpty() -> {
                binding.edtNim.error = resources.getString(R.string.nimerr)
                binding.edtNim.requestFocus()
            }
            nama.isEmpty() -> {
                binding.edtNama.error = resources.getString(R.string.namamhserr)
                binding.edtNama.requestFocus()
            }
            prestasi.isEmpty() -> {
                binding.edtPrestasi.error = resources.getString(R.string.prestasierr)
                binding.edtPrestasi.requestFocus()
            }
            else -> {
                binding.includeProgressBar.root.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.GONE
                if (uri.toString().isEmpty()) {
                    prestasiViewModel.prestasi = Prestasi(idUpdate, nim, nama, prestasi, imageUrlUpdate)
                    simpanUpdatePrestasi()
                } else {
                    prestasiViewModel.foto = Foto(
                        System.currentTimeMillis().toString(),
                        getFileExtension(uri).toString(),
                        uri
                    )
                    prestasiViewModel.prestasi = Prestasi(nama = nama, nim = nim)
                    prestasiViewModel.uploadFoto().observe(this) { imageUrl ->
                        val popupMessageAdminBinding =
                            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahPrestasiActivity))
                        val alertbuilder = AlertDialog.Builder(this@TambahPrestasiActivity)
                        alertbuilder.setView(popupMessageAdminBinding.root)
                        val alertDialog = alertbuilder.create()

                        if (imageUrl != "0") {

                            prestasiViewModel.prestasi = Prestasi(imageUrl = imageUrlUpdate)
                            prestasiViewModel.deleteImage().observe(this) {
                                if (it) {
                                    prestasiViewModel.prestasi = Prestasi(idUpdate, nim, nama, prestasi, imageUrl)
                                    simpanUpdatePrestasi()
                                } else {
                                    alertDialog.show()
                                    popupMessageAdminBinding.tvMessage.text =
                                        resources.getString(R.string.gagalanggota)
                                    popupMessageAdminBinding.btnOke.setOnClickListener {
                                        alertDialog.dismiss()
                                    }
                                }
                            }
                        } else {
                            alertDialog.show()
                            popupMessageAdminBinding.tvMessage.text =
                                resources.getString(R.string.gagalanggota)
                            popupMessageAdminBinding.btnOke.setOnClickListener {
                                alertDialog.dismiss()
                            }
                        }

                    }
                }
            }
        }
    }

    private fun simpanUpdatePrestasi(){
        prestasiViewModel.savePrestasi().observe(this) { it1 ->
            val popupMessageAdminBinding =
                PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahPrestasiActivity))
            val alertbuilder = AlertDialog.Builder(this@TambahPrestasiActivity)
            alertbuilder.setView(popupMessageAdminBinding.root)
            val alertDialog = alertbuilder.create()
            alertDialog.show()

            if (it1 == 1) {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.berhasilprestasi)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    binding.edtNim.text = null
                    binding.edtNama.text = null
                    binding.edtPrestasi.text = null
                    binding.imgFoto.setImageResource(R.drawable.ic_add_blue)
                    this.uri = Uri.EMPTY
                    alertDialog.dismiss()
                    if (update == UPDATE) {
                        finish()
                    }
                }
            } else {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.gagalprestasi)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            binding.includeProgressBar.root.visibility = View.GONE
            binding.layoutContent.visibility = View.VISIBLE
        }
    }
}