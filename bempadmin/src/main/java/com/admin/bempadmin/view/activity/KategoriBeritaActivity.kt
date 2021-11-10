package com.admin.bempadmin.view.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityKategoriBeritaBinding
import com.admin.bempadmin.databinding.LayoutTambahDivisiKategoriBinding
import com.admin.bempadmin.databinding.PopupAlertAdminBinding
import com.admin.bempadmin.helper.KATEGORI
import com.admin.bempadmin.helper.SHARED_PREFERENCES
import com.admin.bempadmin.helper.STATUS_ADMIN
import com.admin.bempadmin.model.Kategori
import com.admin.bempadmin.view.adapter.KategoriAdapter
import com.admin.bempadmin.view.utils.OnClickKategoriEventListener
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.KategoriBeritaViewModel

class KategoriBeritaActivity : AppCompatActivity(), View.OnClickListener,
    OnClickKategoriEventListener {
    private lateinit var binding: ActivityKategoriBeritaBinding
    private lateinit var layoutTambahDivisiBinding: LayoutTambahDivisiKategoriBinding
    private lateinit var kategoriBeritaViewModel: KategoriBeritaViewModel
    private lateinit var kategoriAdapter: KategoriAdapter
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKategoriBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.fab) {
            tampilAddEditKategori(false, Kategori())
        } else if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        }
    }

    override fun updateKategori(kategori: Kategori) {
        tampilAddEditKategori(true, kategori)
        layoutTambahDivisiBinding.edtNama.setText(kategori.nama)
    }

    override fun deleteKategori(kategori: Kategori) {
        val popupAlertAdminBinding =
            PopupAlertAdminBinding.inflate(LayoutInflater.from(this@KategoriBeritaActivity))
        val alertbuilder = AlertDialog.Builder(this@KategoriBeritaActivity)
        alertbuilder.setView(popupAlertAdminBinding.root)
        alertbuilder.setCancelable(true)
        alertDialog = alertbuilder.create()
        alertDialog.show()

        popupAlertAdminBinding.tvMessage.text =
            resources.getString(R.string.warninghapusdivisi, kategori.nama)
        popupAlertAdminBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        popupAlertAdminBinding.btnOke.setOnClickListener {
            binding.includeProgressBar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.GONE

            kategoriBeritaViewModel.kategori = kategori
            kategoriBeritaViewModel.delete().observe(this) {
                if (it) {
                    Toast.makeText(
                        this@KategoriBeritaActivity,
                        "Berhasil di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(this@KategoriBeritaActivity, "Gagal di hapus", Toast.LENGTH_LONG)
                        .show()
                }
                alertDialog.dismiss()
            }
            binding.includeProgressBar.root.visibility = View.GONE
            binding.layoutContent.visibility = View.VISIBLE
        }
    }

    private fun setView() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val superAdmin = sharedPreferences.getString(STATUS_ADMIN, "").toString()

        kategoriBeritaViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@KategoriBeritaActivity)
        ).get(KategoriBeritaViewModel::class.java)
        binding.fab.setOnClickListener(this)
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.kategori_berita)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)

        kategoriAdapter = KategoriAdapter(this@KategoriBeritaActivity, this, superAdmin)
        binding.rvKategori.layoutManager = LinearLayoutManager(this@KategoriBeritaActivity)
        showKategori()
    }

    private fun tampilAddEditKategori(update: Boolean, kategori: Kategori) {
        layoutTambahDivisiBinding =
            LayoutTambahDivisiKategoriBinding.inflate(LayoutInflater.from(this@KategoriBeritaActivity))
        val alertbuilder = AlertDialog.Builder(this@KategoriBeritaActivity)
        alertbuilder.setView(layoutTambahDivisiBinding.root)
        alertbuilder.setCancelable(true)
        alertDialog = alertbuilder.create()
        alertDialog.show()

        layoutTambahDivisiBinding.tvTitle.text = resources.getString(R.string.tambah_kategori)
        layoutTambahDivisiBinding.layoutEdtNama.hint = resources.getString(R.string.nama_kategori)
        layoutTambahDivisiBinding.btnSimpan.setOnClickListener {
            if (update) {
                simpanUpdateKategori(kategori)
            } else {
                simpanDivisi()
            }
        }
    }

    private fun simpanDivisi() {
        val namaDivisi = layoutTambahDivisiBinding.edtNama.text.toString()
        if (namaDivisi.isEmpty()) {
            layoutTambahDivisiBinding.edtNama.error = resources.getString(R.string.namadivisierr)
        } else {
            kategoriBeritaViewModel.getMaxId().observe(this) {
                val kategori = Kategori((it + 1).toString(), namaDivisi)
                kategoriBeritaViewModel.kategori = kategori
                kategoriBeritaViewModel.checkNamaDivisi().observe(this) { status ->
                    when (status) {
                        0 -> {
                            kategoriBeritaViewModel.simpanDivisi().observe(this) { isSuccess ->
                                if (isSuccess) {
                                    Toast.makeText(
                                        this@KategoriBeritaActivity,
                                        "Tersimpan",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    alertDialog.dismiss()
                                } else {
                                    Toast.makeText(
                                        this@KategoriBeritaActivity,
                                        "Gagal menyimpan",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        1 -> {
                            Toast.makeText(
                                this@KategoriBeritaActivity,
                                "Nama divisi sudah ada   ",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@KategoriBeritaActivity,
                                "Maaf, terjadi kesalahan dalam menyimpan data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }
            }
        }
    }

    private fun showKategori() {
        binding.includeProgressBar.root.visibility = View.VISIBLE
        binding.layoutContent.visibility = View.GONE
        kategoriBeritaViewModel.showDivisi().observe(this) { hashMap ->
            when (hashMap["code"]) {
                200 -> {
                    kategoriAdapter.listKategori = hashMap[KATEGORI] as ArrayList<Kategori>
                    binding.rvKategori.adapter = kategoriAdapter
                }
                404 -> {
                    Toast.makeText(
                        this@KategoriBeritaActivity,
                        "Belom ada Divisi",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                else -> {
                    Toast.makeText(
                        this@KategoriBeritaActivity,
                        resources.getString(R.string.errorload),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
            binding.includeProgressBar.root.visibility = View.GONE
            binding.layoutContent.visibility = View.VISIBLE
        }
    }

    private fun simpanUpdateKategori(kategori: Kategori) {
        val namaKategori = layoutTambahDivisiBinding.edtNama.text.toString()
        if (namaKategori.isEmpty()) {
            layoutTambahDivisiBinding.edtNama.error = resources.getString(R.string.namadivisierr)
        } else {
            kategoriBeritaViewModel.kategori = Kategori(kategori.id, namaKategori)
            kategoriBeritaViewModel.simpanDivisi().observe(this) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(
                        this@KategoriBeritaActivity,
                        "Berhasil di ubah",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(
                        this@KategoriBeritaActivity,
                        "Gagal mengubah data",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}