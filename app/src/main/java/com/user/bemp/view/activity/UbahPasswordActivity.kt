package com.user.bemp.view.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.user.bemp.R
import com.user.bemp.databinding.ActivityUbahPasswordBinding
import com.user.bemp.databinding.PopupMessageUserBinding
import com.user.bemp.helper.ID_USER
import com.user.bemp.helper.SHARED_PREFERENCES
import com.user.bemp.model.User
import com.user.bemp.viewmodel.FirebaseViewModelFactory
import com.user.bemp.viewmodel.UserViewModel

class UbahPasswordActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityUbahPasswordBinding
    private lateinit var userViewModel: UserViewModel
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
        userViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@UbahPasswordActivity)
        ).get(UserViewModel::class.java)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun ubahPassword() {
        val password = binding.edtPassword.text.toString().trim()
        val passwordKonfirm = binding.edtPasswordKonfirmasi.text.toString().trim()

        val popupMessageUserBinding =
            PopupMessageUserBinding.inflate(LayoutInflater.from(this@UbahPasswordActivity))
        val alertbuilder = AlertDialog.Builder(this@UbahPasswordActivity)
        alertbuilder.setView(popupMessageUserBinding.root)
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
                popupMessageUserBinding.tvMessage.text =
                    resources.getString(R.string.ubahpasserr)
                popupMessageUserBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            else -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.btnUbah.windowToken, 0)

                binding.layoutContent.visibility = View.GONE
                binding.includeProgressBar.root.visibility = View.VISIBLE
                userViewModel.user = User(
                    id = sharedPreferences.getString(ID_USER, "").toString(),
                    password = password
                )
                userViewModel.ubahPassword().observe(this) {
                    alertDialog.show()
                    if (it) {
                        popupMessageUserBinding.tvMessage.text =
                            resources.getString(R.string.berhasiubahpass)
                        popupMessageUserBinding.btnOke.setOnClickListener {
                            alertDialog.dismiss()
                            binding.layoutContent.visibility = View.VISIBLE
                            binding.includeProgressBar.root.visibility = View.GONE

                            binding.edtPassword.text = null
                            binding.edtPasswordKonfirmasi.text = null
                        }
                    } else {
                        popupMessageUserBinding.tvMessage.text =
                            resources.getString(R.string.gagalubahpass)
                        popupMessageUserBinding.btnOke.setOnClickListener {
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