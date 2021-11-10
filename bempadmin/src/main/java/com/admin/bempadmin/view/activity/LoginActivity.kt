package com.admin.bempadmin.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityLoginBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.helper.*
import com.admin.bempadmin.helper.SESSION
import com.admin.bempadmin.model.Admin
import com.admin.bempadmin.viewmodel.AdminViewModel
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseViewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(v: View?) {
        if (v == binding.btnLogin) {
            login()
        }
    }

    private fun setView() {
        binding.btnLogin.setOnClickListener(this)

        firebaseViewModel =
            ViewModelProvider(viewModelStore, FirebaseViewModelFactory(this@LoginActivity)).get(
                AdminViewModel::class.java
            )
    }

    private fun login() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding.edtEmail.error = resources.getString(R.string.emailerr)
                binding.edtEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.edtPassword.error = resources.getString(R.string.passerr)
                binding.edtPassword.requestFocus()
            }
            else -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.btnLogin.windowToken, 0)

                binding.layoutContent.visibility = View.GONE
                binding.includeProgressBar.root.visibility = View.VISIBLE

                val admin = Admin(email = email, password = password)
                firebaseViewModel.admin = admin
                firebaseViewModel.loginAdmin().observe(this) { hashMap ->
                    val popupMessageAdminBinding =
                        PopupMessageAdminBinding.inflate(LayoutInflater.from(this@LoginActivity))
                    val alertbuilder = AlertDialog.Builder(this@LoginActivity)
                    alertbuilder.setView(popupMessageAdminBinding.root)
                    val alertDialog = alertbuilder.create()

                    binding.layoutContent.visibility = View.VISIBLE
                    binding.includeProgressBar.root.visibility = View.GONE

                    when (hashMap["code"].toString().toInt()) {
                        0 -> {
                            alertDialog.show()
                            popupMessageAdminBinding.tvMessage.text =
                                resources.getString(R.string.loginerror)
                            popupMessageAdminBinding.btnOke.setOnClickListener {
                                alertDialog.dismiss()
                            }
                        }

                        -1 -> {
                            alertDialog.show()
                            popupMessageAdminBinding.tvMessage.text =
                                resources.getString(R.string.loginerror)
                            popupMessageAdminBinding.btnOke.setOnClickListener {
                                alertDialog.dismiss()
                            }
                        }

                        else -> {
                            val admin1 = hashMap["admin"] as Admin
                            val sharedPreferences =
                                getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString(ID_ADMIN, admin1.id)
                            editor.putString(NAMA_ADMIN, admin1.nama)
                            editor.putString(STATUS_ADMIN, admin1.status)
                            editor.putBoolean(SESSION, true)
                            editor.apply()
                            startActivity(Intent(this@LoginActivity, BeritaActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }
    }
}