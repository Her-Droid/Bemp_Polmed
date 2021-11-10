package com.admin.bempadmin.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityDaftarAdminBinding
import com.admin.bempadmin.databinding.PopupAlertAdminBinding
import com.admin.bempadmin.helper.TB_ADMIN
import com.admin.bempadmin.helper.UPDATE
import com.admin.bempadmin.model.Admin
import com.admin.bempadmin.model.Anggota
import com.admin.bempadmin.view.adapter.AdminAdapter
import com.admin.bempadmin.view.utils.OnClickAdminEventListener
import com.admin.bempadmin.viewmodel.AdminViewModel
import com.admin.bempadmin.viewmodel.AnggotaOrganisasiViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory

class DaftarAdminActivity : AppCompatActivity(), View.OnClickListener, OnClickAdminEventListener {
    private lateinit var binding: ActivityDaftarAdminBinding
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var anggotaOrganisasiViewModel: AnggotaOrganisasiViewModel
    private lateinit var adminAdapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.fab) {
            startActivity(Intent(this@DaftarAdminActivity, RegisterAdminActivity::class.java))
        } else if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        }
    }

    override fun updateDivisi(admin: Admin) {
        val intent = Intent(this@DaftarAdminActivity, RegisterAdminActivity::class.java)
        intent.putExtra(TB_ADMIN, admin)
        intent.putExtra(UPDATE, UPDATE)
        startActivity(intent)
    }

    override fun deleteDivisi(admin: Admin) {
        val popupAlertAdminBinding =
            PopupAlertAdminBinding.inflate(LayoutInflater.from(this@DaftarAdminActivity))
        val alertbuilder = AlertDialog.Builder(this@DaftarAdminActivity)
        alertbuilder.setView(popupAlertAdminBinding.root)
        alertbuilder.setCancelable(true)
        val alertDialog = alertbuilder.create()
        alertDialog.show()
        popupAlertAdminBinding.tvMessage.text =
            resources.getString(R.string.hapusadmin, admin.status, admin.nama)
        popupAlertAdminBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        popupAlertAdminBinding.btnOke.setOnClickListener {

            binding.includeProgressBar.root.visibility = View.VISIBLE
            binding.includeToolbar.root.visibility = View.GONE
            binding.fab.visibility = View.GONE
            binding.rvAdmin.visibility = View.GONE

            adminViewModel.admin = admin
            adminViewModel.deleteAdmin().observe(this) {
                if (it) {
                    if (admin.idAnggota == "") {
                        Toast.makeText(
                            this@DaftarAdminActivity,
                            "Admin berhasil di hapus",
                            Toast.LENGTH_LONG
                        )
                            .show()

                        alertDialog.dismiss()
                        binding.includeProgressBar.root.visibility = View.GONE
                        binding.includeToolbar.root.visibility = View.VISIBLE
                        binding.fab.visibility = View.VISIBLE
                        binding.rvAdmin.visibility = View.VISIBLE
                    } else {
                        anggotaOrganisasiViewModel.anggota =
                            Anggota(id = admin.idAnggota, idAdmin = "")
                        anggotaOrganisasiViewModel.updateIdAdmin().observe(this) {
                            Toast.makeText(
                                this@DaftarAdminActivity,
                                "Admin berhasil di hapus",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            alertDialog.dismiss()
                            binding.includeProgressBar.root.visibility = View.GONE
                            binding.includeToolbar.root.visibility = View.VISIBLE
                            binding.fab.visibility = View.VISIBLE
                            binding.rvAdmin.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Toast.makeText(
                        this@DaftarAdminActivity,
                        "Gagal di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    alertDialog.dismiss()
                    binding.includeProgressBar.root.visibility = View.GONE
                    binding.includeToolbar.root.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                    binding.rvAdmin.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setView() {
        adminViewModel =
            ViewModelProvider(
                viewModelStore,
                FirebaseViewModelFactory(this@DaftarAdminActivity)
            ).get(
                AdminViewModel::class.java
            )
        anggotaOrganisasiViewModel =
            ViewModelProvider(
                viewModelStore,
                FirebaseViewModelFactory(this@DaftarAdminActivity)
            ).get(
                AnggotaOrganisasiViewModel::class.java
            )

        binding.includeToolbar.tvTitle.text = resources.getString(R.string.daftaradmin)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.fab.setOnClickListener(this)
        binding.rvAdmin.layoutManager = LinearLayoutManager(this)
        binding.rvAdmin.setHasFixedSize(true)
        adminAdapter = AdminAdapter(this@DaftarAdminActivity, this)

        showAdmin()
    }

    private fun showAdmin() {
        binding.includeProgressBar.root.visibility = View.VISIBLE
        binding.includeToolbar.root.visibility = View.GONE
        binding.fab.visibility = View.GONE
        binding.rvAdmin.visibility = View.GONE

        adminViewModel.showAdmin().observe(this) { hashMap ->
            if (hashMap["code"].toString() == "200") {
                val adminList = hashMap[TB_ADMIN] as ArrayList<Admin>
                adminAdapter.listAdmin = adminList
                binding.rvAdmin.adapter = adminAdapter
            } else {
                Toast.makeText(
                    this@DaftarAdminActivity,
                    "Daftar admin kosong",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            binding.includeProgressBar.root.visibility = View.GONE
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.fab.visibility = View.VISIBLE
            binding.rvAdmin.visibility = View.VISIBLE
        }
    }
}