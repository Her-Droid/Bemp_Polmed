package com.admin.bempadmin.view.activity

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
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
import com.admin.bempadmin.databinding.ActivityTambahBeritaBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.helper.*
import com.admin.bempadmin.model.*
import com.admin.bempadmin.viewmodel.BeritaViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.KategoriBeritaViewModel
import com.bumptech.glide.Glide
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TambahBeritaActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityTambahBeritaBinding
    private lateinit var beritaViewModel: BeritaViewModel
    private lateinit var file: File
    private var uri: Uri = Uri.EMPTY

    private var idUpdate = ""
    private var namaKategori = ""
    private var idKategori = ""
    private var idAdmin = ""
    private var idAdminUpdate = ""
    private var imageUrlUpdate = ""
    private var dateupdate = ""
    private var update = "ADD"

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                if (!uri.toString().isEmpty()) {
                    binding.imgFoto.visibility = View.VISIBLE
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
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.includeToolbar.imgBtnBack -> {
                finish()
            }
            binding.btnTambahKategori -> {
                startActivity(Intent(this@TambahBeritaActivity, KategoriBeritaActivity::class.java))
            }
            binding.btnSimpan -> {
                if (update == UPDATE) {
                    updateBerita()
                } else {
                    simpanBerita()
                }
            }
            binding.btnTambahGambar -> {
                startForResult.launch("image/*")
            }
        }
    }

    private fun setView() {
        PermissionHelper().PermissionHelper(this@TambahBeritaActivity)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        idAdmin = sharedPreferences.getString(ID_ADMIN, "").toString()
        beritaViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@TambahBeritaActivity)
        ).get(BeritaViewModel::class.java)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnTambahKategori.setOnClickListener(this)
        binding.btnSimpan.setOnClickListener(this)
        binding.btnTambahGambar.setOnClickListener(this)

        update = intent.getStringExtra(UPDATE).toString()
        if (update == UPDATE) {
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.ubah_berita)
            val beritaUpdate = intent.getParcelableExtra<BeritaModel>(ISIBERITA)
            if (beritaUpdate != null) {
                if (beritaUpdate.imageUrl.isEmpty()) {
                    binding.imgFoto.visibility = View.GONE
                } else {
                    imageUrlUpdate = beritaUpdate.imageUrl
                    binding.imgFoto.visibility = View.VISIBLE
                    Glide.with(this@TambahBeritaActivity)
                        .load(imageUrlUpdate)
                        .into(binding.imgFoto)
                }

                idUpdate = beritaUpdate.id
                binding.edtTitle.setText(beritaUpdate.judul)
                binding.edtIsiBerita.setText(beritaUpdate.isi)
                namaKategori = beritaUpdate.idKategori
                dateupdate = beritaUpdate.date
                idAdminUpdate = beritaUpdate.idAdmin
            }
        } else {
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.tambah_berita)
        }
        showKategori()
    }

    private fun showKategori() {
        val kategoriBeritaActivity = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@TambahBeritaActivity)
        ).get(KategoriBeritaViewModel::class.java)

        val hashMapKategori: HashMap<String, String> = HashMap()
        kategoriBeritaActivity.showDivisi().observe(this) { hashMap ->
            if (hashMap["code"] == 200) {
                val listKategori = hashMap[KATEGORI] as ArrayList<Kategori>
                val listNamaKategori = ArrayList<String>()

                val idKategoriArray = arrayOfNulls<String>(listKategori.size)
                val namaKategoriArray = arrayOfNulls<String>(listKategori.size)
                for (i in 0 until listKategori.size) {
                    listNamaKategori.add(listKategori[i].nama)
                    idKategoriArray[i] = listKategori[i].id
                    namaKategoriArray[i] = listKategori[i].nama
                    namaKategoriArray[i]?.let {
                        idKategoriArray[i]?.let { it1 ->
                            hashMapKategori.put(
                                it,
                                it1
                            )
                        }
                    }
                }

                binding.spinKategori.setItems(listNamaKategori)
                if (namaKategori != "") {
                    for (i in 0 until listNamaKategori.size) {
                        if (listKategori[i].nama == namaKategori) {
                            binding.spinKategori.selectItemByIndex(i)
                            idKategori = listKategori[i].id
                            break
                        }
                    }
                }
            }

            binding.spinKategori.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
                idKategori = hashMapKategori[newItem].toString()
            }
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
        Glide.with(this@TambahBeritaActivity)
            .load(bitmapKtp)
            .fitCenter()
            .into(binding.imgFoto)
    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    private fun simpanBerita() {
        val judul = binding.edtTitle.text.toString().trim()
        val isi = binding.edtIsiBerita.text.toString().trim()

        when {
            judul.isEmpty() -> {
                binding.edtTitle.error = resources.getString(R.string.judulerr)
                binding.edtTitle.requestFocus()
            }
            idKategori.isEmpty() -> {
                binding.spinKategori.error = resources.getString(R.string.kategorikosong)
                binding.spinKategori.requestFocus()
            }
            isi.isEmpty() -> {
                binding.edtIsiBerita.error = resources.getString(R.string.isierr)
                binding.edtIsiBerita.requestFocus()
            }
            else -> {
                binding.includeProgressBar.root.visibility = View.VISIBLE
                binding.includeToolbar.root.visibility = View.GONE
                binding.layoutContent.visibility = View.GONE

                beritaViewModel.getMaxId().observe(this) {
                    val date = SimpleDateFormat(
                        "dd MMMM yyyy, HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())
                    if (uri.toString().isEmpty()) {
                        beritaViewModel.berita =
                            Berita(
                                (it + 1).toString(),
                                judul,
                                "",
                                isi,
                                date,
                                idKategori,
                                idAdmin,
                                ""
                            )
                        simpanUpdateBerita()
                    } else {
                        beritaViewModel.foto = Foto(
                            System.currentTimeMillis().toString(),
                            getFileExtension(uri).toString(),
                            uri
                        )
                        beritaViewModel.uploadFoto().observe(this) { imageUrl ->
                            beritaViewModel.berita =
                                Berita(
                                    (it + 1).toString(),
                                    judul,
                                    imageUrl,
                                    isi,
                                    date,
                                    idKategori,
                                    idAdmin,
                                    ""
                                )
                            simpanUpdateBerita()
                        }
                    }
                }
            }
        }
    }

    private fun updateBerita() {
        val judul = binding.edtTitle.text.toString().trim()
        val isi = binding.edtIsiBerita.text.toString().trim()

        when {
            judul.isEmpty() -> {
                binding.edtTitle.error = resources.getString(R.string.judulerr)
                binding.edtTitle.requestFocus()
            }
            idKategori.isEmpty() -> {
                binding.spinKategori.error = resources.getString(R.string.kategorikosong)
                binding.spinKategori.requestFocus()
            }
            isi.isEmpty() -> {
                binding.edtIsiBerita.error = resources.getString(R.string.isierr)
                binding.edtIsiBerita.requestFocus()
            }
            else -> {
                binding.includeProgressBar.root.visibility = View.VISIBLE
                binding.includeToolbar.root.visibility = View.GONE
                binding.layoutContent.visibility = View.GONE

                if (uri.toString().isEmpty()) {
                    beritaViewModel.berita = Berita(
                        id = idUpdate,
                        judul = judul,
                        imageUrl = "",
                        isi = isi,
                        date = dateupdate,
                        idKategori = idKategori,
                        idAdminUpdate,
                        postingUpdateAdmin = idAdmin
                    )
                    simpanUpdateBerita()
                } else {
                    beritaViewModel.foto = Foto(
                        System.currentTimeMillis().toString(),
                        getFileExtension(uri).toString(),
                        uri
                    )

                    beritaViewModel.uploadFoto().observe(this) { imageUrl ->
                        val popupMessageAdminBinding =
                            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahBeritaActivity))
                        val alertbuilder = AlertDialog.Builder(this@TambahBeritaActivity)
                        alertbuilder.setView(popupMessageAdminBinding.root)
                        val alertDialog = alertbuilder.create()

                        if (imageUrl != "0") {
                            if (imageUrlUpdate.isEmpty()) {
                                beritaViewModel.berita = Berita(
                                    id = idUpdate,
                                    judul = judul,
                                    imageUrl = imageUrl,
                                    isi = isi,
                                    date = dateupdate,
                                    idKategori = idKategori,
                                    idAdminUpdate,
                                    postingUpdateAdmin = idAdmin
                                )
                                simpanUpdateBerita()
                            } else {
                                beritaViewModel.berita = Berita(imageUrl = imageUrlUpdate)
                                beritaViewModel.deleteImage().observe(this) {
                                    if (it) {
                                        beritaViewModel.berita = Berita(
                                            id = idUpdate,
                                            judul = judul,
                                            imageUrl = imageUrl,
                                            isi = isi,
                                            date = dateupdate,
                                            idKategori = idKategori,
                                            idAdminUpdate,
                                            postingUpdateAdmin = idAdmin
                                        )
                                        simpanUpdateBerita()
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

    private fun simpanUpdateBerita() {
        beritaViewModel.saveBerita().observe(this) {
            val popupMessageAdminBinding =
                PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahBeritaActivity))
            val alertbuilder = AlertDialog.Builder(this@TambahBeritaActivity)
            alertbuilder.setView(popupMessageAdminBinding.root)
            val alertDialog = alertbuilder.create()
            alertDialog.show()
            if (it == 1) {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.berhasilberita)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    binding.edtTitle.text = null
                    binding.edtIsiBerita.text = null
                    if (!uri.toString().isEmpty()) {
                        binding.imgFoto.visibility = View.GONE
                        binding.imgFoto.setImageResource(0)
                        this.uri = Uri.EMPTY
                    }
                    alertDialog.dismiss()
                    if (update == UPDATE){
                        finish()
                    }
                }
            } else {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.gagalberita)
                popupMessageAdminBinding.btnOke.setOnClickListener { alertDialog.dismiss() }
            }

            binding.includeProgressBar.root.visibility = View.GONE
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.VISIBLE
        }
    }
}