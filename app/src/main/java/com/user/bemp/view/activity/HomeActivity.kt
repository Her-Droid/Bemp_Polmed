package com.user.bemp.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationBarView
import com.user.bemp.R
import com.user.bemp.databinding.ActivityHomeBinding
import com.user.bemp.helper.ISIBERITA
import com.user.bemp.helper.NAMA_USER
import com.user.bemp.helper.SHARED_PREFERENCES
import com.user.bemp.model.Berita
import com.user.bemp.view.adapter.BeritaAdapter
import com.user.bemp.viewmodel.BeritaViewModel
import com.user.bemp.viewmodel.FirebaseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    View.OnClickListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var beritaAdapter: BeritaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.cvStrukturOrganisasi) {
            startActivity(Intent(this@HomeActivity, StrukturOrganisasiActivity::class.java))
        } else if (p0 == binding.cvPerlombaan) {
            startActivity(Intent(this@HomeActivity, PrestasiActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lomba -> {
                intent = Intent(this@HomeActivity, LombaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_profil -> {
                intent = Intent(this@HomeActivity, ProfilActivity::class.java)
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
        binding.tvNama.text = sharedPreferences.getString(NAMA_USER, "")

        binding.bottomNav.menu.findItem(R.id.menu_home).isChecked = true
        binding.bottomNav.setOnItemSelectedListener(this)
        binding.cvStrukturOrganisasi.setOnClickListener(this)
        binding.cvPerlombaan.setOnClickListener(this)

        binding.rvBerita.layoutManager = LinearLayoutManager(this@HomeActivity)
        binding.rvBerita.isNestedScrollingEnabled = false
        binding.rvBerita.setHasFixedSize(false)
        beritaAdapter = BeritaAdapter(this@HomeActivity)

        val date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        binding.tvDate.text = resources.getString(R.string.date, date)

        showBerita()
    }

    private fun showBerita() {
        binding.scrollView.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE

        val beritaViewModel =
            ViewModelProvider(viewModelStore, FirebaseViewModelFactory(this@HomeActivity)).get(
                BeritaViewModel::class.java
            )
        beritaViewModel.showBerita().observe(this) { hashMap ->
            when (hashMap["code"]) {
                200 -> {
                    beritaAdapter.listBerita = hashMap[ISIBERITA] as ArrayList<Berita>
                    binding.rvBerita.adapter = beritaAdapter
                }
                403 -> {
                    Toast.makeText(
                        this@HomeActivity,
                        resources.getString(R.string.errorload),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                404 -> {
                    beritaAdapter.listBerita = ArrayList()
                    binding.rvBerita.adapter = beritaAdapter
                }
            }
            binding.scrollView.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }
}