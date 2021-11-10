package com.user.bemp.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationBarView
import com.user.bemp.R
import com.user.bemp.databinding.ActivityProfilBinding
import com.user.bemp.helper.*
import com.user.bemp.model.FotoProfil
import com.user.bemp.model.User
import com.user.bemp.viewmodel.FirebaseViewModelFactory
import com.user.bemp.viewmodel.UserViewModel

class ProfilActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    View.OnClickListener {
    private lateinit var binding: ActivityProfilBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userViewModel: UserViewModel

    private var imageUrl = ""

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                saveProfil(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.imgProfil -> {
                startForResult.launch("image/*")
            }
            binding.cvPassword -> {
                startActivity(Intent(this@ProfilActivity, UbahPasswordActivity::class.java))
            }
            binding.cvTentangAplikasi -> {
                startActivity(Intent(this@ProfilActivity, TentangAplikasiActivity::class.java))
            }
            binding.cvKeluar -> {
                val sharedPreferences =
                    getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(ID_USER, null)
                editor.putString(NAMA_USER, null)
                editor.putString(NIM_USER, null)
                editor.putString(PRODI_USER, null)
                editor.putString(FOTO_PROFIL, null)
                editor.putBoolean(SESSION, false)
                editor.apply()
                startActivity(Intent(this@ProfilActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                intent = Intent(this@ProfilActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_lomba -> {
                intent = Intent(this@ProfilActivity, LombaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            else -> return false
        }
        return true
    }

    private fun setView() {
        PermissionHelper().PermissionHelper(this@ProfilActivity)

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        imageUrl = sharedPreferences.getString(FOTO_PROFIL, "").toString()
        binding.tvNama.text = sharedPreferences.getString(NAMA_USER, "")
        binding.tvNim.text = sharedPreferences.getString(NIM_USER, "")
        binding.tvProdi.text = sharedPreferences.getString(PRODI_USER, "")
        Glide.with(this@ProfilActivity)
            .load(imageUrl)
            .error(R.drawable.ic_default_user)
            .into(binding.imgProfil)

        binding.bottomNav.menu.findItem(R.id.menu_profil).isChecked = true
        binding.bottomNav.setOnItemSelectedListener(this)

        binding.imgProfil.setOnClickListener(this)
        binding.cvPassword.setOnClickListener(this)
        binding.cvTentangAplikasi.setOnClickListener(this)
        binding.cvKeluar.setOnClickListener(this)
    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    private fun saveProfil(uri: Uri) {
        binding.layout1.visibility = View.GONE
        binding.layout2.visibility = View.GONE
        binding.bottomNav.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE
        userViewModel =
            ViewModelProvider(viewModelStore, FirebaseViewModelFactory(this@ProfilActivity)).get(
                UserViewModel::class.java
            )
        userViewModel.fotoProfil = FotoProfil(
            System.currentTimeMillis().toString(),
            getFileExtension(uri).toString(),
            uri
        )
        userViewModel.uploadFoto().observe(this) { imageUrlBaru ->
            if (imageUrlBaru.isEmpty()) {
                binding.layout1.visibility = View.VISIBLE
                binding.layout2.visibility = View.VISIBLE
                binding.bottomNav.visibility = View.VISIBLE
                binding.includeProgressBar.root.visibility = View.GONE
                Toast.makeText(this@ProfilActivity, "Gagal pasang foto profil", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (imageUrl.isEmpty()) {
                    updateFoto(imageUrlBaru)
                } else {
                    userViewModel.user = User(imageUrl = imageUrl)
                    userViewModel.deleteImage().observe(this) {
                        if (it) {
                            updateFoto(imageUrlBaru)
                        } else {
                            Toast.makeText(
                                this@ProfilActivity,
                                "Gagal pasang foto profil",
                                Toast.LENGTH_LONG
                            ).show()
                            binding.layout1.visibility = View.VISIBLE
                            binding.layout2.visibility = View.VISIBLE
                            binding.bottomNav.visibility = View.VISIBLE
                            binding.includeProgressBar.root.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun updateFoto(imageUrlBaru: String) {
        userViewModel.user =
            User(id = sharedPreferences.getString(ID_USER, "").toString(), imageUrl = imageUrlBaru)
        userViewModel.updateFotoUrl().observe(this) { it1 ->
            if (it1) {
                Toast.makeText(
                    this@ProfilActivity,
                    "Foto profil berhasil diubah",
                    Toast.LENGTH_LONG
                ).show()
                Glide.with(this@ProfilActivity)
                    .load(imageUrlBaru)
                    .error(R.drawable.ic_default_user)
                    .into(binding.imgProfil)
                val editor = sharedPreferences.edit()
                editor.putString(FOTO_PROFIL, imageUrlBaru)
                editor.apply()
            } else {
                Toast.makeText(this@ProfilActivity, "Gagal pasang foto profil", Toast.LENGTH_LONG)
                    .show()
            }
            binding.layout1.visibility = View.VISIBLE
            binding.layout2.visibility = View.VISIBLE
            binding.bottomNav.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }
}