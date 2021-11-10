package com.admin.bempadmin.view.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.viewmodel.AdminViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory

import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityRegisterAdminBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.helper.TB_ADMIN
import com.admin.bempadmin.helper.UPDATE
import com.admin.bempadmin.model.Admin


class RegisterAdminActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRegisterAdminBinding
    private lateinit var adminViewModel: AdminViewModel

    private var idAdmin = ""
    private var statusAdmin = ""
    private var idAnggota = ""
    private var update = "ADD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        } else if (p0 == binding.btnDaftar) {
            if (update == UPDATE) {
                updateAdmin()
            } else {
                register()
            }
        }
    }

    private fun setView() {
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnDaftar.setOnClickListener(this)
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.pendaftaran_admin)
        adminViewModel =
            ViewModelProvider(
                viewModelStore,
                FirebaseViewModelFactory(this@RegisterAdminActivity)
            ).get(
                AdminViewModel::class.java
            )

        update = intent.getStringExtra(UPDATE).toString()
        if (update == UPDATE) {
            val admin = intent.getParcelableExtra<Admin>(TB_ADMIN)
            if (admin != null) {
                binding.edtNamaLengkap.setText(admin.nama)
                binding.edtEmail.setText(admin.email)
                binding.edtPassword.setText(admin.password)
                idAdmin = admin.id
                statusAdmin = admin.status
                idAnggota = admin.idAnggota
            }
        }
        showStatusAdmin()
    }

    private fun showStatusAdmin() {
        when {
            statusAdmin.equals("Super Admin", ignoreCase = true) -> {
                binding.spinAdmin.selectItemByIndex(0)
            }
            statusAdmin.equals("Admin", ignoreCase = true) -> {
                binding.spinAdmin.selectItemByIndex(1)
            }
        }
        binding.spinAdmin.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            statusAdmin = newItem
        }
    }

    private fun register() {
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@RegisterAdminActivity))
        val alertbuilder = AlertDialog.Builder(this@RegisterAdminActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        val namaLengkap = binding.edtNamaLengkap.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when {
            namaLengkap.isEmpty() -> {
                binding.edtNamaLengkap.error = resources.getString(R.string.namadepanerr)
                binding.edtNamaLengkap.requestFocus()
            }
            email.isEmpty() -> {
                binding.edtEmail.error = resources.getString(R.string.emailerr)
                binding.edtEmail.requestFocus()
            }
            statusAdmin.isEmpty() -> {
                alertDialog.show()
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.statusadminerr)
                popupMessageAdminBinding.btnOke.setOnClickListener { alertDialog.dismiss() }
            }
            else -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.btnDaftar.windowToken, 0)

                binding.layoutContent.visibility = View.GONE
                binding.includeProgressBar.root.visibility = View.VISIBLE

                adminViewModel.getMaxId().observe(this) {
                    adminViewModel.admin =
                        Admin((it + 1).toString(), "", namaLengkap, email, password, statusAdmin)
                    saveAdmin()
                }
            }
        }
    }

    private fun saveAdmin(){
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@RegisterAdminActivity))
        val alertbuilder = AlertDialog.Builder(this@RegisterAdminActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        adminViewModel.checkEmail().observe(this) { statusEmail ->
            if (statusEmail == 0) {
                adminViewModel.registerAdmin().observe(this) { isSuccess ->
                    alertDialog.show()
                    if (isSuccess) {
                        if (update == UPDATE){
                            popupMessageAdminBinding.tvMessage.text =
                                resources.getString(R.string.berhasilupdateadmin, statusAdmin, binding.edtNamaLengkap.text.toString().trim())
                        } else {
                            popupMessageAdminBinding.tvMessage.text =
                                resources.getString(R.string.berhasilregister, statusAdmin)
                        }
                        popupMessageAdminBinding.btnOke.setOnClickListener {
                            alertDialog.dismiss()
                            binding.edtNamaLengkap.text = null
                            binding.edtEmail.text = null
                            binding.spinAdmin.text = null
                            statusAdmin = ""
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
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.includeProgressBar.root.visibility = View.GONE
                }
            } else {
                alertDialog.show()
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.emailterdaftar)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
                binding.layoutContent.visibility = View.VISIBLE
                binding.includeProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun updateAdmin() {
        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@RegisterAdminActivity))
        val alertbuilder = AlertDialog.Builder(this@RegisterAdminActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        val namaLengkap = binding.edtNamaLengkap.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when {
            namaLengkap.isEmpty() -> {
                binding.edtNamaLengkap.error = resources.getString(R.string.namadepanerr)
                binding.edtNamaLengkap.requestFocus()
            }
            email.isEmpty() -> {
                binding.edtEmail.error = resources.getString(R.string.emailerr)
                binding.edtEmail.requestFocus()
            }
            statusAdmin.isEmpty() -> {
                alertDialog.show()
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.statusadminerr)
                popupMessageAdminBinding.btnOke.setOnClickListener { alertDialog.dismiss() }
            }
            else -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.btnDaftar.windowToken, 0)

                binding.layoutContent.visibility = View.GONE
                binding.includeProgressBar.root.visibility = View.VISIBLE

                adminViewModel.admin = Admin(idAdmin, idAnggota, namaLengkap, email, password, statusAdmin)
                saveAdmin()
            }
        }
    }
}