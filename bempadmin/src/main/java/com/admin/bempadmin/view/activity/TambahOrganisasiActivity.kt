package com.admin.bempadmin.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityTambahOrganisasiBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.viewmodel.DivisiViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import android.provider.MediaStore

import android.text.TextUtils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import java.io.File
import java.lang.Exception
import android.graphics.Bitmap
import android.graphics.Matrix

import android.media.ExifInterface
import java.io.IOException
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.util.Log
import com.bumptech.glide.Glide
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import android.webkit.MimeTypeMap
import com.admin.bempadmin.helper.*
import com.admin.bempadmin.model.*
import com.admin.bempadmin.viewmodel.AdminViewModel
import com.admin.bempadmin.viewmodel.AnggotaOrganisasiViewModel


class TambahOrganisasiActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding: ActivityTambahOrganisasiBinding
    private lateinit var anggotaOrganisasiViewModel: AnggotaOrganisasiViewModel
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var file: File
    private var uri: Uri = Uri.EMPTY

    private var idAnggotaUpdate = ""
    private var idDivisi = ""
    private var namaDivisi = ""
    private var imageUrlUpdate = ""
    private var statusAdmin = ""
    private var idAdmin = ""
    private var update = "ADD"
    private var namaAnggota = ""
    private var email = ""
    private var password = "bemp123456"
    private var superAdmin = ""

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
        binding = ActivityTambahOrganisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnTambahDivisi -> {
                startActivity(Intent(this@TambahOrganisasiActivity, DivisiActivity::class.java))
            }
            binding.includeToolbar.imgBtnBack -> {
                finish()
            }
            binding.btnSimpan -> {
                if (update == UPDATE) {
                    updateAnggota()
                } else {
                    simpanAnggota()
                }
            }
            binding.imgFoto -> {
                startForResult.launch("image/*")
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        binding.tvSisaText.text = (300 - p3).toString()
        binding.tvSisaText.text = (binding.tvSisaText.text.toString().toInt() - p1).toString()
    }

    override fun afterTextChanged(p0: Editable?) {}

    private fun setView() {
        PermissionHelper().PermissionHelper(this@TambahOrganisasiActivity)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        superAdmin = sharedPreferences.getString(STATUS_ADMIN, "").toString()

        anggotaOrganisasiViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@TambahOrganisasiActivity)
        ).get(AnggotaOrganisasiViewModel::class.java)

        adminViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@TambahOrganisasiActivity)
        ).get(
            AdminViewModel::class.java
        )

        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnTambahDivisi.setOnClickListener(this)
        binding.btnSimpan.setOnClickListener(this)
        binding.imgFoto.setOnClickListener(this)
        binding.edtBio.addTextChangedListener(this)

        update = intent.getStringExtra(UPDATE).toString()
        if (update == UPDATE) {
            val list = (resources.getStringArray(R.array.statusAdminUpdate)).toList()
            binding.spinAdmin.setItems(list)

            val anggotaUpdate = intent.getParcelableExtra<AnggotaAdmin>(ANGGOTA)
            if (anggotaUpdate != null) {
                if (anggotaUpdate.motto.isEmpty()) {
                    binding.tvSisaText.text = resources.getString(R.string._100)
                } else {
                    binding.tvSisaText.text = (100 - anggotaUpdate.motto.length).toString()
                }

                idAnggotaUpdate = anggotaUpdate.id
                imageUrlUpdate = anggotaUpdate.imageUrl
                email = anggotaUpdate.email
                binding.edtNama.setText(anggotaUpdate.nama)
                binding.edtBio.setText(anggotaUpdate.motto)
                binding.edtEmail.setText(email)
                Glide.with(this@TambahOrganisasiActivity)
                    .load(imageUrlUpdate)
                    .into(binding.imgFoto)
                namaDivisi = anggotaUpdate.idDivisi
                idAdmin = anggotaUpdate.idAdmin
                password = anggotaUpdate.password
                statusAdmin = anggotaUpdate.statusAdmin
            }
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.ubah_anggota)
        } else {
            binding.tvSisaText.text = resources.getString(R.string._100)
            binding.includeToolbar.tvTitle.text = resources.getString(R.string.tambah_anggota)
            val list = (resources.getStringArray(R.array.statusAdmin)).toList()
            binding.spinAdmin.setItems(list)
        }

        showDivisi()
        showAdmin()
    }

    private fun showDivisi() {
        val divisiViewModel =
            ViewModelProvider(
                viewModelStore,
                FirebaseViewModelFactory(this@TambahOrganisasiActivity)
            ).get(
                DivisiViewModel::class.java
            )
        val hashMapDivisi: HashMap<String, String> = HashMap()
        divisiViewModel.showDivisi().observe(this) { hashMap ->
            if (hashMap["code"] == 200) {
                val listDivisi = hashMap[DIVISI] as ArrayList<Divisi>
                val listNamaDivisi = ArrayList<String>()

                val idDivisiArray = arrayOfNulls<String>(listDivisi.size)
                val namaDivisiArray = arrayOfNulls<String>(listDivisi.size)
                for (i in 0 until listDivisi.size) {
                    listNamaDivisi.add(listDivisi[i].nama)
                    idDivisiArray[i] = listDivisi[i].id
                    namaDivisiArray[i] = listDivisi[i].nama
                    namaDivisiArray[i]?.let {
                        idDivisiArray[i]?.let { it1 ->
                            hashMapDivisi.put(
                                it,
                                it1
                            )
                        }
                    }
                }
                binding.spinDivisi.setItems(listNamaDivisi)
                if (namaDivisi != "") {
                    for (i in 0 until listDivisi.size) {
                        if (listDivisi[i].nama == namaDivisi) {
                            binding.spinDivisi.selectItemByIndex(i)
                            idDivisi = listDivisi[i].id
                            break
                        }
                    }
                }
            }
            binding.spinDivisi.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
                idDivisi = hashMapDivisi[newItem].toString()
            }
        }
    }

    private fun showAdmin() {
        when {
            statusAdmin.equals("Super Admin", ignoreCase = true) -> {
                binding.spinAdmin.selectItemByIndex(0)
                binding.layoutEmail.visibility = View.VISIBLE
            }
            statusAdmin.equals("Admin", ignoreCase = true) -> {
                binding.spinAdmin.selectItemByIndex(1)
                binding.layoutEmail.visibility = View.VISIBLE
            }
            else -> {
                binding.layoutEmail.visibility = View.GONE
            }
        }
        binding.spinAdmin.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, newItem ->
            statusAdmin = newItem
            if (newIndex == 0 || newIndex == 1) {
                binding.layoutEmail.visibility = View.VISIBLE
            } else {
                binding.layoutEmail.visibility = View.GONE
            }
        }

        if (superAdmin == SUPER_ADMIN){
            binding.layoutEmail.visibility = View.VISIBLE
            binding.spinAdmin.visibility = View.VISIBLE
        } else {
            binding.layoutEmail.visibility = View.GONE
            binding.spinAdmin.visibility = View.GONE
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
        Glide.with(this@TambahOrganisasiActivity)
            .load(bitmapKtp)
            .into(binding.imgFoto)
    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    private fun simpanAnggota() {
        namaAnggota = binding.edtNama.text.toString().trim()
        val bio = binding.edtBio.text.toString().trim()

        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        when {
            namaAnggota.isEmpty() -> {
                binding.edtNama.error = resources.getString(R.string.namanggotaerr)
                binding.edtNama.requestFocus()
            }
            idDivisi.isEmpty() -> {
                alertDialog.show()
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.divisierr)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            uri.toString().isEmpty() -> {
                alertDialog.show()

                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.fotokosong)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            bio.isEmpty() -> {
                binding.edtBio.error = resources.getString(R.string.bioerr)
                binding.edtBio.requestFocus()
            }
            else -> {
                val isEmail: Boolean
                if (statusAdmin.equals(
                        "Super Admin",
                        ignoreCase = true
                    ) || statusAdmin.equals("Admin", ignoreCase = true)
                ) {
                    email = binding.edtEmail.text.toString().trim()
                    if (email.isEmpty()) {
                        isEmail = false
                        binding.edtEmail.error = resources.getString(R.string.emailerr)
                        binding.edtEmail.requestFocus()
                    } else {
                        isEmail = true
                    }
                } else {
                    isEmail = true
                }
                if (isEmail) {
                    binding.includeProgressBar.root.visibility = View.VISIBLE
                    binding.layoutContent.visibility = View.GONE

                    adminViewModel.admin = Admin(email = email)
                    adminViewModel.checkEmail().observe(this) { statusEmail ->
                        if (statusEmail == 0) {
                            anggotaOrganisasiViewModel.getMaxId().observe(this) {
                                idAnggotaUpdate = (it + 1).toString()
                                anggotaOrganisasiViewModel.foto = Foto(
                                    System.currentTimeMillis().toString(),
                                    getFileExtension(uri).toString(),
                                    uri
                                )

                                anggotaOrganisasiViewModel.uploadFoto().observe(this) { imageUrl ->
                                    if (imageUrl != "0") {
                                        anggotaOrganisasiViewModel.anggota = Anggota(
                                            idAnggotaUpdate,
                                            namaAnggota,
                                            idDivisi,
                                            bio,
                                            imageUrl,
                                            idAdmin
                                        )
                                        simpanUpdateAnggota()
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
                                resources.getString(R.string.emailterdaftar)
                            popupMessageAdminBinding.btnOke.setOnClickListener {
                                alertDialog.dismiss()
                            }

                            binding.includeProgressBar.root.visibility = View.GONE
                            binding.layoutContent.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun updateAnggota() {
        namaAnggota = binding.edtNama.text.toString().trim()
        val bio = binding.edtBio.text.toString().trim()

        when {
            namaAnggota.isEmpty() -> {
                binding.edtNama.error = resources.getString(R.string.namanggotaerr)
                binding.edtNama.requestFocus()
            }
            idDivisi.isEmpty() -> {
                binding.spinDivisi.error = resources.getString(R.string.divisierr)
                binding.spinDivisi.requestFocus()
            }
            bio.isEmpty() -> {
                binding.edtBio.error = resources.getString(R.string.bioerr)
                binding.edtBio.requestFocus()
            }
            else -> {
                val isEmail: Boolean
                val emailupdate = binding.edtEmail.text.toString().trim()
                if (statusAdmin.equals(
                        "Super Admin",
                        ignoreCase = true
                    ) || statusAdmin.equals("Admin", ignoreCase = true)
                ) {
                    if (emailupdate.isEmpty()) {
                        isEmail = false
                        binding.edtEmail.error = resources.getString(R.string.emailerr)
                        binding.edtEmail.requestFocus()
                    } else {
                        isEmail = true
                    }
                } else {
                    isEmail = true
                }
                if (isEmail) {
                    binding.includeProgressBar.root.visibility = View.VISIBLE
                    binding.layoutContent.visibility = View.GONE

                    val popupMessageAdminBinding =
                        PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
                    val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
                    alertbuilder.setView(popupMessageAdminBinding.root)
                    val alertDialog = alertbuilder.create()

                    if (email == emailupdate){
                        updates(bio)
                    } else {
                        adminViewModel.admin = Admin(email = emailupdate)
                        adminViewModel.checkEmail().observe(this) { statusEmail ->
                            if (statusEmail == 0) {
                                email = emailupdate
                                updates(bio)
                            } else {
                                alertDialog.show()
                                popupMessageAdminBinding.tvMessage.text =
                                    resources.getString(R.string.emailterdaftar)
                                popupMessageAdminBinding.btnOke.setOnClickListener {
                                    alertDialog.dismiss()
                                }
                                binding.includeProgressBar.root.visibility = View.GONE
                                binding.layoutContent.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updates(bio: String){
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        if (uri.toString().isEmpty()) {
            anggotaOrganisasiViewModel.anggota = Anggota(
                idAnggotaUpdate,
                namaAnggota,
                idDivisi,
                bio,
                imageUrlUpdate,
                idAdmin
            )
            simpanUpdateAnggota()
        } else {
            anggotaOrganisasiViewModel.foto = Foto(
                System.currentTimeMillis().toString(),
                getFileExtension(uri).toString(),
                uri
            )

            anggotaOrganisasiViewModel.uploadFoto().observe(this) { imageUrl ->

                if (imageUrl != "0") {

                    anggotaOrganisasiViewModel.anggota =
                        Anggota(imageUrl = imageUrlUpdate)
                    anggotaOrganisasiViewModel.deleteImage().observe(this) {
                        if (it) {
                            anggotaOrganisasiViewModel.anggota = Anggota(
                                idAnggotaUpdate,
                                namaAnggota,
                                idDivisi,
                                bio,
                                imageUrl,
                                idAdmin
                            )
                            simpanUpdateAnggota()
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

    private fun simpanUpdateAnggota() {
        anggotaOrganisasiViewModel.saveAnggota().observe(this) { it1 ->
            val popupMessageAdminBinding =
                PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
            val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
            alertbuilder.setView(popupMessageAdminBinding.root)
            val alertDialog = alertbuilder.create()

            if (it1 == 1) {
                if (update == UPDATE) {
                    if (statusAdmin != "") {
                        if (idAdmin == "" && !statusAdmin.equals(
                                "Bukan Keduanya",
                                ignoreCase = true
                            )
                        ) {
                            password = "bemp123456"
                            saveAdmin()
                        } else {
                            if (statusAdmin.equals("Bukan Keduanya", ignoreCase = true)) {
                                if (idAdmin != "") {
                                    deleteAdmin()
                                } else {
                                    alertDialog.show()
                                    popupMessageAdminBinding.tvMessage.text =
                                        resources.getString(R.string.berhasilubahdataanggota)
                                    popupMessageAdminBinding.btnOke.setOnClickListener {
                                        statusAdmin = ""
                                        binding.edtNama.text = null
                                        binding.edtEmail.text = null
                                        binding.edtBio.text = null
                                        binding.spinAdmin.text = null
                                        binding.imgFoto.setImageResource(R.drawable.ic_add_blue)
                                        this.uri = Uri.EMPTY
                                        alertDialog.dismiss()
                                        if (update == UPDATE) {
                                            finish()
                                        }
                                    }
                                }
                            } else {
                                updateAdmin()
                            }
                        }
                    } else {
                        alertDialog.show()
                        popupMessageAdminBinding.tvMessage.text =
                            resources.getString(R.string.berhasilubahdataanggota)
                        popupMessageAdminBinding.btnOke.setOnClickListener {
                            statusAdmin = ""
                            binding.edtNama.text = null
                            binding.edtEmail.text = null
                            binding.edtBio.text = null
                            binding.spinAdmin.text = null
                            binding.imgFoto.setImageResource(R.drawable.ic_add_blue)
                            this.uri = Uri.EMPTY
                            alertDialog.dismiss()
                            if (update == UPDATE) {
                                finish()
                            }
                        }
                    }
                } else {
                    if (statusAdmin == "") {
                        alertDialog.show()
                        popupMessageAdminBinding.tvMessage.text =
                            resources.getString(R.string.berhasilanggota)
                        popupMessageAdminBinding.btnOke.setOnClickListener {
                            statusAdmin = ""
                            binding.edtNama.text = null
                            binding.edtEmail.text = null
                            binding.edtBio.text = null
                            binding.spinAdmin.text = null
                            binding.imgFoto.setImageResource(R.drawable.ic_add_blue)
                            this.uri = Uri.EMPTY
                            alertDialog.dismiss()
                        }
                    } else {
                        saveAdmin()
                    }
                }
            } else {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.gagalanggota)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            binding.includeProgressBar.root.visibility = View.GONE
            binding.layoutContent.visibility = View.VISIBLE
        }
    }

    private fun saveAdmin() {
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        adminViewModel.getMaxId().observe(this) {
            val idAdmin = (it + 1).toString()
            val admin =
                Admin(idAdmin, idAnggotaUpdate, namaAnggota, email, password, statusAdmin)
            adminViewModel.admin = admin
            adminViewModel.registerAdmin().observe(this) { isSuccess ->
                if (isSuccess) {
                    anggotaOrganisasiViewModel.anggota =
                        Anggota(id = idAnggotaUpdate, idAdmin = idAdmin)
                    updateIdAdmin(1)
                } else {
                    alertDialog.show()
                    popupMessageAdminBinding.tvMessage.text =
                        resources.getString(R.string.gagalanggota)
                    popupMessageAdminBinding.btnOke.setOnClickListener {
                        alertDialog.dismiss()
                    }
                }
                binding.layoutContent.visibility = View.VISIBLE
                binding.includeProgressBar.root.visibility = View.GONE
            }

        }

    }

    private fun updateIdAdmin(status: Int) {
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        anggotaOrganisasiViewModel.updateIdAdmin().observe(this) { it1 ->
            alertDialog.show()
            if (it1) {
                if (status == 1) {
                    popupMessageAdminBinding.tvMessage.text =
                        resources.getString(R.string.berhasilsimpananggotadanregisteradmin)
                } else {
                    popupMessageAdminBinding.tvMessage.text =
                        resources.getString(R.string.berhasilubahanggotadanhapusadmin)
                }
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    statusAdmin = ""
                    binding.edtNama.text = null
                    binding.edtEmail.text = null
                    binding.edtBio.text = null
                    binding.spinAdmin.text = null
                    binding.imgFoto.setImageResource(R.drawable.ic_add_blue)
                    this.uri = Uri.EMPTY
                    alertDialog.dismiss()
                    if (update == UPDATE) {
                        finish()
                    }
                }
            } else {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.gagalanggota)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }
    }

    private fun updateAdmin() {
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        adminViewModel.admin = Admin(id = idAdmin, email = email, status = statusAdmin)
        adminViewModel.updateStatusAdmin().observe(this) {
            alertDialog.show()
            if (it) {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.berhasilubahdataanggota)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                    finish()
                }
            } else {
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.gagalanggota)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            binding.layoutContent.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }

    private fun deleteAdmin() {
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@TambahOrganisasiActivity))
        val alertbuilder = AlertDialog.Builder(this@TambahOrganisasiActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        adminViewModel.admin = Admin(id = idAdmin)
        adminViewModel.deleteAdmin().observe(this) {
            if (it) {
                anggotaOrganisasiViewModel.anggota =
                    Anggota(id = idAnggotaUpdate, idAdmin = "")
                updateIdAdmin(2)
            } else {
                alertDialog.show()
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.gagalhapusadmin)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            binding.layoutContent.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }
}

