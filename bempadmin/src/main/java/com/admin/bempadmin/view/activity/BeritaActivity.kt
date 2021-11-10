package com.admin.bempadmin.view.activity

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityBeritaBinding
import com.admin.bempadmin.databinding.PopupAlertAdminBinding
import com.admin.bempadmin.helper.*
import com.admin.bempadmin.model.Berita
import com.admin.bempadmin.model.BeritaModel
import com.admin.bempadmin.view.adapter.BeritaAdapter
import com.admin.bempadmin.view.utils.OnClickBeritaEventListener
import com.admin.bempadmin.viewmodel.BeritaViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.google.android.material.navigation.NavigationBarView
import java.text.SimpleDateFormat
import java.util.*


class BeritaActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    View.OnClickListener, OnClickBeritaEventListener {
    private lateinit var binding: ActivityBeritaBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var beritaViewModel: BeritaViewModel
    private lateinit var beritaAdapter: BeritaAdapter

    private var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onBackPressed() {
        if (isFABOpen) {
            closeFABMenu()
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.fabKategori) {
            closeFABMenu()
            startActivity(Intent(this@BeritaActivity, KategoriBeritaActivity::class.java))
        } else if (p0 == binding.fabBerita) {
            closeFABMenu()
            startActivity(Intent(this@BeritaActivity, TambahBeritaActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lomba -> {
                intent = Intent(this@BeritaActivity, LombaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_prestasi -> {
                intent = Intent(this@BeritaActivity, PrestasiActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_profil -> {
                intent = Intent(this@BeritaActivity, ProfilActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            else -> return false
        }
        return true
    }

    override fun updateBerita(beritaModel: BeritaModel) {
        val intent = Intent(this@BeritaActivity, TambahBeritaActivity::class.java)
        intent.putExtra(ISIBERITA, beritaModel)
        intent.putExtra(UPDATE, UPDATE)
        startActivity(intent)
    }

    override fun deleteBerita(beritaModel: BeritaModel) {
        val popupAlertAdminBinding =
            PopupAlertAdminBinding.inflate(LayoutInflater.from(this@BeritaActivity))
        val alertbuilder = AlertDialog.Builder(this@BeritaActivity)
        alertbuilder.setView(popupAlertAdminBinding.root)
        alertbuilder.setCancelable(true)
        val alertDialog = alertbuilder.create()
        alertDialog.show()
        popupAlertAdminBinding.tvMessage.text = resources.getString(R.string.warninghapusberita)
        popupAlertAdminBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        popupAlertAdminBinding.btnOke.setOnClickListener {
            binding.layoutContent.visibility = View.GONE
            binding.coordinatLayout.visibility = View.GONE
            binding.includeProgressBar.root.visibility = View.VISIBLE

            beritaViewModel.berita = Berita(id = beritaModel.id)
            beritaViewModel.delete().observe(this) {
                alertDialog.dismiss()
                if (it) {
                    Toast.makeText(
                        this@BeritaActivity,
                        "Berhasil di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this@BeritaActivity,
                        "Gagal di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                binding.layoutContent.visibility = View.VISIBLE
                binding.coordinatLayout.visibility = View.VISIBLE
                binding.includeProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun setView() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val superAdmin = sharedPreferences.getString(STATUS_ADMIN, "").toString()

        beritaViewModel =
            ViewModelProvider(viewModelStore, FirebaseViewModelFactory(this@BeritaActivity)).get(
                BeritaViewModel::class.java
            )

        binding.bottomNav.menu.findItem(R.id.menu_berita).isChecked = true
        binding.bottomNav.setOnItemSelectedListener(this)
        binding.tvNama.text = sharedPreferences.getString(NAMA_ADMIN, "")
        binding.tvStatusAdmin.text = resources.getString(R.string.selamat_datang_admin, superAdmin)

        val date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        binding.tvDate.text = resources.getString(R.string.date, date)

        binding.fabBerita.setOnClickListener(this)
        binding.fabKategori.setOnClickListener(this)
        binding.fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        binding.rvBerita.layoutManager = LinearLayoutManager(this)
        binding.rvBerita.isNestedScrollingEnabled = false
        binding.rvBerita.setHasFixedSize(false)
        beritaAdapter = BeritaAdapter(this@BeritaActivity, this, superAdmin)
        showBerita()
    }

    private fun showBerita() {
        binding.layoutContent.visibility = View.GONE
        binding.coordinatLayout.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE

        beritaViewModel.showBerita().observe(this) { hashMap ->
            when (hashMap["code"]) {
                200 -> {
                    beritaAdapter.listBerita = hashMap[ISIBERITA] as ArrayList<BeritaModel>
                    binding.rvBerita.adapter = beritaAdapter
                }
                403 -> {
                    Toast.makeText(
                        this@BeritaActivity,
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
            binding.layoutContent.visibility = View.VISIBLE
            binding.coordinatLayout.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }

    /**
     * Digunakan untuk menampilkan floating action button
     */
    private fun showFABMenu() {
        isFABOpen = true
        binding.fabLayoutBerita.visibility = View.VISIBLE
        binding.fabLayoutKategori.visibility = View.VISIBLE
        binding.fabBGLayout.visibility = View.VISIBLE
        binding.fab.animate().rotationBy(180.0f)
        binding.fabLayoutKategori.animate().translationY(-resources.getDimension(R.dimen.fabdivisi))
        binding.fabLayoutBerita.animate().translationY(-resources.getDimension(R.dimen.fabanggota))
    }

    /**
     * Digunakan untuk menutup floating action button
     */
    private fun closeFABMenu() {
        isFABOpen = false
        binding.fabBGLayout.visibility = View.GONE
        binding.fab.animate().rotation(0f)
        binding.fabLayoutBerita.animate().translationY(0f)
        binding.fabLayoutKategori.animate().translationY(0f)
        binding.fabLayoutKategori.animate().translationY(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (!isFABOpen) {
                        binding.fabLayoutKategori.visibility = View.GONE
                        binding.fabLayoutBerita.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
    }

}