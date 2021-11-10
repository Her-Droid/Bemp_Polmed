package com.user.bemp.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.user.bemp.R
import com.user.bemp.databinding.ActivityLoginBinding
import com.user.bemp.databinding.PopupMessageUserBinding
import com.user.bemp.helper.*
import com.user.bemp.helper.SESSION
import com.user.bemp.model.User

import com.user.bemp.viewmodel.UserViewModel
import com.user.bemp.viewmodel.FirebaseViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(v: View?) {
        if (v == binding.tvRegister) {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        } else if (v == binding.btnLogin) {
            login()
        }
    }

    private fun setView() {
        binding.tvRegister.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)

        userViewModel =
            ViewModelProvider(viewModelStore, FirebaseViewModelFactory(this@LoginActivity)).get(
                UserViewModel::class.java
            )
    }

    private fun login() {
        val nim = binding.tvNim.text.toString().trim()
        val password = binding.tvPassword.text.toString().trim()

        when {
            nim.isEmpty() -> {
                binding.tvNim.error = resources.getString(R.string.emailerr)
                binding.tvNim.requestFocus()
            }
            password.isEmpty() -> {
                binding.tvPassword.error = resources.getString(R.string.passerr)
                binding.tvPassword.requestFocus()
            }
            else -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.btnLogin.windowToken, 0)

                binding.layoutContent.visibility = View.GONE
                binding.includeProgressBar.root.visibility = View.VISIBLE

                val user = User(nim = nim, password = password)
                userViewModel.user = user
                userViewModel.loginUser().observe(this) { hashMap ->
                    val popupMessageUserBinding =
                        PopupMessageUserBinding.inflate(LayoutInflater.from(this@LoginActivity))
                    val alertbuilder = AlertDialog.Builder(this@LoginActivity)
                    alertbuilder.setView(popupMessageUserBinding.root)
                    val alertDialog = alertbuilder.create()

                    binding.layoutContent.visibility = View.VISIBLE
                    binding.includeProgressBar.root.visibility = View.GONE

                    when (hashMap["code"]) {
                        0 -> {
                            alertDialog.show()
                            popupMessageUserBinding.tvMessage.text =
                                resources.getString(R.string.loginerror)
                            popupMessageUserBinding.btnOke.setOnClickListener {
                                alertDialog.dismiss()
                            }
                        }

                        -1 -> {
                            alertDialog.show()
                            popupMessageUserBinding.tvMessage.text =
                                resources.getString(R.string.loginerror)
                            popupMessageUserBinding.btnOke.setOnClickListener {
                                alertDialog.dismiss()
                            }
                        }

                        else -> {
                            val user1 = hashMap["user"] as User
                            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,
                                Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString(ID_USER, user1.id)
                            editor.putString(NAMA_USER, user1.namaDepan+" "+user1.namaBelakang)
                            editor.putString(NIM_USER, user1.nim)
                            editor.putString(PRODI_USER, user1.prodi)
                            editor.putString(FOTO_PROFIL, user1.imageUrl)
                            editor.putBoolean(SESSION, true)
                            editor.apply()
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }
    }
}