package com.admin.bempadmin.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.databinding.ActivityStrukturOrganisasiBinding
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import android.animation.Animator
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.PopupAlertAdminBinding
import com.admin.bempadmin.helper.*
import com.admin.bempadmin.model.Admin
import com.admin.bempadmin.model.Anggota
import com.admin.bempadmin.model.AnggotaAdmin
import com.admin.bempadmin.view.adapter.AnggotaOrganisasiAdapter
import com.admin.bempadmin.view.utils.OnClickAnggotaEventListener
import com.admin.bempadmin.viewmodel.AdminViewModel
import com.admin.bempadmin.viewmodel.AnggotaOrganisasiViewModel
import com.admin.bempadmin.viewmodel.BioOrganisasiViewModel


class StrukturOrganisasiActivity : AppCompatActivity(), View.OnClickListener,
    OnClickAnggotaEventListener {
    private lateinit var binding: ActivityStrukturOrganisasiBinding
    private lateinit var bioOrganisasiViewModel: BioOrganisasiViewModel
    private lateinit var anggotaOrganisasiViewModel: AnggotaOrganisasiViewModel
    private lateinit var anggotaOrganisasiAdapter: AnggotaOrganisasiAdapter
    private var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStrukturOrganisasiBinding.inflate(layoutInflater)
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
        when (p0) {
            binding.imgBtnEditBio -> {
                intent = Intent(
                    this@StrukturOrganisasiActivity,
                    EditBioStrukturOrganisasiActivity::class.java
                )
                intent.putExtra(BIO, binding.tvBio.text.toString())
                startActivity(intent)
            }
            binding.includeToolbar.imgBtnBack -> {
                finish()
            }
            binding.fabDivisi -> {
                startActivity(Intent(this@StrukturOrganisasiActivity, DivisiActivity::class.java))
                closeFABMenu()
            }
            binding.fabAnggota -> {
                startActivity(
                    Intent(
                        this@StrukturOrganisasiActivity,
                        TambahOrganisasiActivity::class.java
                    )
                )
                closeFABMenu()
            }
        }
    }

    override fun updateDivisi(anggotaAdmin: AnggotaAdmin) {
        val intent = Intent(this@StrukturOrganisasiActivity, TambahOrganisasiActivity::class.java)
        intent.putExtra(ANGGOTA, anggotaAdmin)
        intent.putExtra(UPDATE, UPDATE)
        startActivity(intent)
    }

    override fun deleteDivisi(anggotaAdmin: AnggotaAdmin) {
        val popupAlertAdminBinding =
            PopupAlertAdminBinding.inflate(LayoutInflater.from(this@StrukturOrganisasiActivity))
        val alertbuilder = AlertDialog.Builder(this@StrukturOrganisasiActivity)
        alertbuilder.setView(popupAlertAdminBinding.root)
        alertbuilder.setCancelable(true)
        val alertDialog = alertbuilder.create()
        alertDialog.show()
        popupAlertAdminBinding.tvMessage.text =
            resources.getString(R.string.warninghapusanggota, anggotaAdmin.nama)
        popupAlertAdminBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        popupAlertAdminBinding.btnOke.setOnClickListener {
            binding.includeToolbar.root.visibility = View.GONE
            binding.layoutContent.visibility = View.GONE
            binding.coordinatLayout.visibility = View.GONE
            binding.includeProgressBar.root.visibility = View.VISIBLE

            anggotaOrganisasiViewModel.anggota = Anggota(id = anggotaAdmin.id, imageUrl = anggotaAdmin.imageUrl)
            if (anggotaAdmin.idAdmin == "") {
                alertDialog.dismiss()
                deleteAnggota()
            } else {
                val adminViewModel = ViewModelProvider(
                    viewModelStore,
                    FirebaseViewModelFactory(this@StrukturOrganisasiActivity)
                ).get(
                    AdminViewModel::class.java
                )
                adminViewModel.admin = Admin(id = anggotaAdmin.idAdmin)
                adminViewModel.deleteAdmin().observe(this) {
                    alertDialog.dismiss()
                    if (it) {
                        deleteAnggota()
                    } else {
                        Toast.makeText(
                            this@StrukturOrganisasiActivity,
                            "Gagal di hapus",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun setView() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val superAdmin = sharedPreferences.getString(STATUS_ADMIN, "").toString()

        bioOrganisasiViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@StrukturOrganisasiActivity)
        ).get(BioOrganisasiViewModel::class.java)
        anggotaOrganisasiViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@StrukturOrganisasiActivity)
        ).get(AnggotaOrganisasiViewModel::class.java)

        binding.includeToolbar.tvTitle.text = resources.getString(R.string.struktur_organisasi)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.imgBtnEditBio.setOnClickListener(this)
        binding.fabDivisi.setOnClickListener(this)
        binding.fabAnggota.setOnClickListener(this)
        binding.rvStrukturOrganisasi.layoutManager =
            LinearLayoutManager(this@StrukturOrganisasiActivity)
        binding.rvStrukturOrganisasi.isNestedScrollingEnabled = false
        binding.rvStrukturOrganisasi.setHasFixedSize(false)
        anggotaOrganisasiAdapter = AnggotaOrganisasiAdapter(this@StrukturOrganisasiActivity, this, superAdmin)

        binding.fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        loadBio()
    }

    /**
     * Digunakan untuk menampilkan floating action button
     */
    private fun showFABMenu() {
        isFABOpen = true
        binding.fabLayoutAnggota.visibility = View.VISIBLE
        binding.fabLayoutDivisi.visibility = View.VISIBLE
        binding.fabBGLayout.visibility = View.VISIBLE
        binding.fab.animate().rotationBy(180.0f)
        binding.fabLayoutDivisi.animate().translationY(-resources.getDimension(R.dimen.fabdivisi))
        binding.fabLayoutAnggota.animate().translationY(-resources.getDimension(R.dimen.fabanggota))
    }

    /**
     * Digunakan untuk menutup floating action button
     */
    private fun closeFABMenu() {
        isFABOpen = false
        binding.fabBGLayout.visibility = View.GONE
        binding.fab.animate().rotation(0f)
        binding.fabLayoutAnggota.animate().translationY(0f)
        binding.fabLayoutDivisi.animate().translationY(0f)
        binding.fabLayoutDivisi.animate().translationY(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (!isFABOpen) {
                        binding.fabLayoutDivisi.visibility = View.GONE
                        binding.fabLayoutAnggota.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
    }

    private fun loadBio() {
        binding.includeToolbar.root.visibility = View.GONE
        binding.layoutContent.visibility = View.GONE
        binding.coordinatLayout.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE

        bioOrganisasiViewModel.showBioStrukturOrgnisasi().observe(this) { bio ->
            if (bio.isEmpty()) {
                binding.tvBio.text = resources.getString(R.string.tentang_struktur_organisasi)
            } else {
                binding.tvBio.text = bio
            }
            loadAnggota()
        }
    }

    private fun loadAnggota() {
        anggotaOrganisasiViewModel.showAnggota().observe(this) { hashmap ->
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.VISIBLE
            binding.coordinatLayout.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
            when (hashmap["code"]) {
                200 -> {
                    anggotaOrganisasiAdapter.listAnggota = hashmap[ANGGOTA] as ArrayList<AnggotaAdmin>
                    binding.rvStrukturOrganisasi.adapter = anggotaOrganisasiAdapter
                }
                403 -> {
                    Toast.makeText(
                        this@StrukturOrganisasiActivity,
                        resources.getString(R.string.errorload),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                404 -> {
                    anggotaOrganisasiAdapter.listAnggota = ArrayList()
                    binding.rvStrukturOrganisasi.adapter = anggotaOrganisasiAdapter
                }
            }
        }
    }

    private fun deleteAnggota() {
        anggotaOrganisasiViewModel.delete().observe(this) {
            if (it) {
                Toast.makeText(
                    this@StrukturOrganisasiActivity,
                    "Berhasil di hapus",
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                Toast.makeText(
                    this@StrukturOrganisasiActivity,
                    "Gagal di hapus",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.VISIBLE
            binding.coordinatLayout.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }
}