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
import com.admin.bempadmin.databinding.ActivityDivisiBinding
import com.admin.bempadmin.databinding.LayoutTambahDivisiKategoriBinding
import com.admin.bempadmin.databinding.PopupAlertAdminBinding
import com.admin.bempadmin.helper.DIVISI
import com.admin.bempadmin.helper.SHARED_PREFERENCES
import com.admin.bempadmin.helper.STATUS_ADMIN
import com.admin.bempadmin.model.Divisi
import com.admin.bempadmin.view.adapter.DivisiAdapter
import com.admin.bempadmin.view.utils.OnClickDivisiEventListener
import com.admin.bempadmin.viewmodel.DivisiViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory

class DivisiActivity : AppCompatActivity(), View.OnClickListener, OnClickDivisiEventListener {
    private lateinit var binding: ActivityDivisiBinding
    private lateinit var layoutTambahDivisiBinding: LayoutTambahDivisiKategoriBinding
    private lateinit var divisiViewModel: DivisiViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var divisiAdapter: DivisiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDivisiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.fab -> {
                tampilAddEditDivisi(false, Divisi())
            }
            binding.includeToolbar.imgBtnBack -> {
                finish()
            }
        }
    }

    override fun updateDivisi(divisi: Divisi) {
        tampilAddEditDivisi(true, divisi)
        layoutTambahDivisiBinding.edtNama.setText(divisi.nama)
    }

    override fun deleteDivisi(divisi: Divisi) {
        val popupAlertAdminBinding =
            PopupAlertAdminBinding.inflate(LayoutInflater.from(this@DivisiActivity))
        val alertbuilder = AlertDialog.Builder(this@DivisiActivity)
        alertbuilder.setView(popupAlertAdminBinding.root)
        alertbuilder.setCancelable(true)
        alertDialog = alertbuilder.create()
        alertDialog.show()

        popupAlertAdminBinding.tvMessage.text =
            resources.getString(R.string.warninghapusdivisi, divisi.nama)
        popupAlertAdminBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        popupAlertAdminBinding.btnOke.setOnClickListener {
            binding.includeProgressBar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.GONE

            divisiViewModel.divisi = divisi
            divisiViewModel.delete().observe(this) {
                if (it) {
                    Toast.makeText(this@DivisiActivity, "Berhasil di hapus", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this@DivisiActivity, "Gagal di hapus", Toast.LENGTH_LONG)
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

        divisiViewModel =
            ViewModelProvider(viewModelStore, FirebaseViewModelFactory(this@DivisiActivity)).get(
                DivisiViewModel::class.java
            )
        binding.fab.setOnClickListener(this)
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.struktur_organisasi)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)

        divisiAdapter = DivisiAdapter(this@DivisiActivity, this, superAdmin)
        binding.rvDivisi.layoutManager = LinearLayoutManager(this@DivisiActivity)

        showDivisi()
    }

    private fun showDivisi() {
        binding.includeProgressBar.root.visibility = View.VISIBLE
        binding.layoutContent.visibility = View.GONE
        divisiViewModel.showDivisi().observe(this) { hashMap ->
            when (hashMap["code"]) {
                200 -> {
                    divisiAdapter.divisiList = hashMap[DIVISI] as ArrayList<Divisi>
                    binding.rvDivisi.adapter = divisiAdapter
                }
                404 -> {
                    Toast.makeText(this@DivisiActivity, "Belom ada Divisi", Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    Toast.makeText(
                        this@DivisiActivity,
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

    private fun tampilAddEditDivisi(update: Boolean, divisi: Divisi) {
        layoutTambahDivisiBinding =
            LayoutTambahDivisiKategoriBinding.inflate(LayoutInflater.from(this@DivisiActivity))
        val alertbuilder = AlertDialog.Builder(this@DivisiActivity)
        alertbuilder.setView(layoutTambahDivisiBinding.root)
        alertbuilder.setCancelable(true)
        alertDialog = alertbuilder.create()
        alertDialog.show()

        layoutTambahDivisiBinding.tvTitle.text = resources.getString(R.string.tambah_divisi)
        layoutTambahDivisiBinding.layoutEdtNama.hint = resources.getString(R.string.nama_divisi)
        layoutTambahDivisiBinding.btnSimpan.setOnClickListener {
            if (update) {
                simpanUpdateDivisi(divisi)
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
            divisiViewModel.getMaxId().observe(this) {
                val divisi = Divisi((it + 1).toString(), namaDivisi)
                divisiViewModel.divisi = divisi
                divisiViewModel.checkNamaDivisi().observe(this) { status ->
                    when (status) {
                        0 -> {
                            divisiViewModel.simpanDivisi().observe(this) { isSuccess ->
                                if (isSuccess) {
                                    Toast.makeText(
                                        this@DivisiActivity,
                                        "Tersimpan",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    alertDialog.dismiss()
                                } else {
                                    Toast.makeText(
                                        this@DivisiActivity,
                                        "Gagal menyimpan",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        1 -> {
                            Toast.makeText(
                                this@DivisiActivity,
                                "Nama divisi sudah ada   ",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@DivisiActivity,
                                "Maaf, terjadi kesalahan dalam menyimpan data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }
            }
        }
    }

    private fun simpanUpdateDivisi(divisi: Divisi) {
        val namaDivisi = layoutTambahDivisiBinding.edtNama.text.toString()
        if (namaDivisi.isEmpty()) {
            layoutTambahDivisiBinding.edtNama.error = resources.getString(R.string.namadivisierr)
        } else {
            divisiViewModel.divisi = Divisi(divisi.id, namaDivisi)
            divisiViewModel.simpanDivisi().observe(this) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(this@DivisiActivity, "Berhasil di ubah", Toast.LENGTH_LONG)
                        .show()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(
                        this@DivisiActivity,
                        "Gagal mengubah data",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}