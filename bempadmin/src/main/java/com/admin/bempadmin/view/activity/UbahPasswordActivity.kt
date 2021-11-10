package com.admin.bempadmin.view.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityUbahPasswordBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.helper.ID_ADMIN
import com.admin.bempadmin.helper.SHARED_PREFERENCES
import com.admin.bempadmin.model.Admin
import com.admin.bempadmin.viewmodel.AdminViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory

class UbahPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUbahPasswordBinding
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        } else if (p0 == binding.btnUbah) {
            ubahPassword()
        }
    }

    private fun setView() {
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.ubah_password)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnUbah.setOnClickListener(this)
        adminViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@UbahPasswordActivity)
        ).get(AdminViewModel::class.java)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun ubahPassword() {
        val password = binding.edtPassword.text.toString().trim()
        val passwordKonfirm = binding.edtPasswordKonfirmasi.text.toString().trim()

        val popupMessageAdminBinding =
            PopupMessageAdminBinding.inflate(LayoutInflater.from(this@UbahPasswordActivity))
        val alertbuilder = AlertDialog.Builder(this@UbahPasswordActivity)
        alertbuilder.setView(popupMessageAdminBinding.root)
        val alertDialog = alertbuilder.create()

        when {
            password.isEmpty() -> {
                binding.edtPassword.error = resources.getString(R.string.passerr)
                binding.edtPassword.requestFocus()
            }
            passwordKonfirm.isEmpty() -> {
                binding.edtPasswordKonfirmasi.error =
                    resources.getString(R.string.konfirmasipasserr)
                binding.edtPasswordKonfirmasi.requestFocus()
            }
            password != passwordKonfirm -> {
                alertDialog.show()
                popupMessageAdminBinding.tvMessage.text =
                    resources.getString(R.string.ubahpasserr)
                popupMessageAdminBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            else -> {
                binding.layoutContent.visibility = View.GONE
                binding.includeProgressBar.root.visibility = View.VISIBLE
                adminViewModel.admin = Admin(
                    id = sharedPreferences.getString(ID_ADMIN, "").toString(),
                    password = password
                )
                adminViewModel.ubahPassword().observe(this) {
                    alertDialog.show()
                    if (it) {
                        popupMessageAdminBinding.tvMessage.text =
                            resources.getString(R.string.berhasiubahpass)
                        popupMessageAdminBinding.btnOke.setOnClickListener {
                            alertDialog.dismiss()
                            binding.layoutContent.visibility = View.VISIBLE
                            binding.includeProgressBar.root.visibility = View.GONE

                            binding.edtPassword.text = null
                            binding.edtPasswordKonfirmasi.text = null
                        }
                    } else {
                        popupMessageAdminBinding.tvMessage.text =
                            resources.getString(R.string.gagalubahpass)
                        popupMessageAdminBinding.btnOke.setOnClickListener {
                            alertDialog.dismiss()
                            binding.layoutContent.visibility = View.VISIBLE
                            binding.includeProgressBar.root.visibility = View.GONE

                            binding.edtPassword.text = null
                            binding.edtPasswordKonfirmasi.text = null
                        }
                    }
                }
            }
        }
    }

}