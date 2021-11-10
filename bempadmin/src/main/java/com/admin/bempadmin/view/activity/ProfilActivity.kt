package com.admin.bempadmin.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityProfilBinding
import com.admin.bempadmin.helper.*
import com.admin.bempadmin.helper.SESSION
import com.google.android.material.navigation.NavigationBarView

class ProfilActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    View.OnClickListener {
    private lateinit var binding: ActivityProfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.cvTambahBerita -> {
                startActivity(Intent(this@ProfilActivity, TambahBeritaActivity::class.java))
            }
            binding.cvTambahLomba -> {
                startActivity(Intent(this@ProfilActivity, TambahLombaActivity::class.java))
            }
            binding.cvTambahPrestasi -> {
                startActivity(Intent(this@ProfilActivity, TambahPrestasiActivity::class.java))
            }
            binding.cvStrukturOrganisasi -> {
                startActivity(Intent(this@ProfilActivity, StrukturOrganisasiActivity::class.java))
            }
            binding.cvSuperAdmin -> {
                startActivity(Intent(this@ProfilActivity, DaftarAdminActivity::class.java))
            }
            binding.cvPassword -> {
                startActivity(Intent(this@ProfilActivity, UbahPasswordActivity::class.java))
            }
            binding.cvTentangAplikasi -> {
                startActivity(Intent(this@ProfilActivity, TentangAplikasiActivity::class.java))
            }
            binding.cvKeluar -> {
                val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(ID_ADMIN, null)
                editor.putString(NAMA_ADMIN, null)
                editor.putString(STATUS_ADMIN, null)
                editor.putBoolean(SESSION, false)
                editor.apply()
                startActivity(Intent(this@ProfilActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_berita -> {
                intent = Intent(this@ProfilActivity, BeritaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_lomba -> {
                intent = Intent(this@ProfilActivity, PrestasiActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_prestasi -> {
                intent = Intent(this@ProfilActivity, PrestasiActivity::class.java)
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
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences.getString(STATUS_ADMIN,"").toString().equals("Super Admin", ignoreCase = true)){
            binding.cvSuperAdmin.visibility = View.VISIBLE
        } else {
            binding.cvSuperAdmin.visibility = View.GONE
        }
        binding.bottomNav.menu.findItem(R.id.menu_profil).isChecked = true
        binding.bottomNav.setOnItemSelectedListener(this)

        binding.cvTambahBerita.setOnClickListener(this)
        binding.cvTambahLomba.setOnClickListener(this)
        binding.cvTambahPrestasi.setOnClickListener(this)
        binding.cvStrukturOrganisasi.setOnClickListener(this)
        binding.cvSuperAdmin.setOnClickListener(this)
        binding.cvPassword.setOnClickListener(this)
        binding.cvTentangAplikasi.setOnClickListener(this)
        binding.cvKeluar.setOnClickListener(this)
    }
}