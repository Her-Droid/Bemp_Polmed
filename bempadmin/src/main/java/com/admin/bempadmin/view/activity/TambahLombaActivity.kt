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
import com.admin.bempadmin.databinding.ActivityTambahLombaBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.helper.PermissionHelper
import com.admin.bempadmin.helper.TB_LOMBA
import com.admin.bempadmin.helper.UPDATE
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.Lomba
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.LombaViewModel
import com.bumptech.glide.Glide
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception

class TambahLombaActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTambahLombaBinding
    private lateinit var lombaViewModel: LombaViewModel
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
        binding = ActivityTambahLombaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.includeToolbar.imgBtnBack -> {
                finish()
            }
            binding.imgFoto -> {
                startForResult.launch("image/*")
            }
            binding.btnSimpan -> {
                if (update == UPDATE) {
                    updateLomba()
                } else {
                    simpanLomba()
                }
            }
        }
    }

    private fun setView() {
        PermissionHelper().PermissionHelper(this@TambahLombaActivity)
        lombaViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@TambahLombaActivity)
        ).get(LombaViewModel::class.java)

        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnSimpan.setOnClickListener(this)
        binding.imgFoto.setOnClickListener(this)

        update = intent.getStringExtra(UPDATE).toString()
        if (update == UPDATE) {
            val lombaUpdate = intent.getParcelableExtra<Lomba>(TB_LOMBA)
            if (lombaUpdate != null) {
                idUpdate = lombaUpdate.id
                imageUrlUpdate = lombaUpdate.imageUrl
                binding.edtJudulLomba.setText(lombaUpdate.judul)
                binding.edtDeskripsi.setText(lombaUpdate.deskripsi)
                Glide.with(this@TambahLombaActivity)
                    .load(imageUrlUpdate)
                    .into(binding.imgFoto)
            }
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.update_lomba)
        } else {
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.tambah_lomba)
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
        Glide.with(this@TambahLombaActivity)
            .load(bitmapKtp)
            .fitCenter()
            .into(binding.imgFoto)
    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    private fun simpanLomba() {
        val judul = binding.edtJudulLomba.text.toString().trim()
        val deskripsi = binding.edtDeskripsi.text.toString().trim()

        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahLombaActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahLombaActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        when {
            judul.isEmpty() -> {
                binding.edtJudulLomba.error = resources.getString(R.string.judullombaerr)
                binding.edtJudulLomba.requestFocus()
            }
            deskripsi.isEmpty() -> {
                binding.edtDeskripsi.error = resources.getString(R.string.deskripsierr)
                binding.edtDeskripsi.requestFocus()
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
                binding.includeToolbar.root.visibility = View.GONE
                binding.layoutContent.visibility = View.GONE

                lombaViewModel.getMaxId().observe(this) {
                    lombaViewModel.foto = Foto(
                        System.currentTimeMillis().toString(),
                        getFileExtension(uri).toString(),
                        uri
                    )
                    lombaViewModel.uploadFoto().observe(this) { imageUrl ->
                        if (imageUrl != "0") {
                            lombaViewModel.lomba =
                                Lomba((it + 1).toString(), judul, deskripsi, imageUrl)
                            simpanUpdateLomba()
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

    private fun updateLomba() {
        val judul = binding.edtJudulLomba.text.toString().trim()
        val deskripsi = binding.edtDeskripsi.text.toString().trim()
        when {
            judul.isEmpty() -> {
                binding.edtJudulLomba.error = resources.getString(R.string.judullombaerr)
                binding.edtJudulLomba.requestFocus()
            }
            deskripsi.isEmpty() -> {
                binding.edtDeskripsi.error = resources.getString(R.string.deskripsierr)
                binding.edtDeskripsi.requestFocus()
            }

            else -> {
                binding.includeProgressBar.root.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.GONE
                if (uri.toString().isEmpty()) {
                    lombaViewModel.lomba = Lomba(idUpdate, judul, deskripsi, imageUrlUpdate)
                    simpanUpdateLomba()
                } else {
                    lombaViewModel.foto = Foto(
                        System.currentTimeMillis().toString(),
                        getFileExtension(uri).toString(),
                        uri
                    )
                    lombaViewModel.uploadFoto().observe(this) { imageUrl ->
                        val popupMessageAdminBinding =
                            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahLombaActivity))
                        val alertbuilder = AlertDialog.Builder(this@TambahLombaActivity)
                        alertbuilder.setView(popupMessageAdminBinding.root)
                        val alertDialog = alertbuilder.create()

                        if (imageUrl != "0") {

                            lombaViewModel.lomba = Lomba(imageUrl = imageUrlUpdate)
                            lombaViewModel.deleteImage().observe(this) {
                                if (it) {
                                    lombaViewModel.lomba = Lomba(
                                        idUpdate,
                                        judul,
                                        deskripsi,
                                        imageUrl,
                                    )
                                    simpanUpdateLomba()
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

    private fun simpanUpdateLomba() {
        lombaViewModel.saveLomba().observe(this) { it1 ->
            val popupMessageAdminBinding =
                PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahLombaActivity))
            val alertbuilder = AlertDialog.Builder(this@TambahLombaActivity)
            alertbuilder.setView(popupMessageAdminBinding.root)
            val alertDialog = alertbuilder.create()
            alertDialog.show()

            if (it1 == 1) {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.berhasillomba)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    binding.edtJudulLomba.text = null
                    binding.edtDeskripsi.text = null
                    binding.imgFoto.setImageResource(R.drawable.ic_add_blue)
                    this.uri = Uri.EMPTY
                    alertDialog.dismiss()
                    if (update == UPDATE) {
                        finish()
                    }
                }
            } else {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.gagallomba)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            binding.includeProgressBar.root.visibility = View.GONE
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.VISIBLE
        }
    }
}