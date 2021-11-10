package com.user.bemp.view.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.user.bemp.R
import com.user.bemp.databinding.ActivityRegisterBinding
import com.user.bemp.databinding.PopupMessageUserBinding
import com.user.bemp.model.User
import com.user.bemp.viewmodel.UserViewModel
import com.user.bemp.viewmodel.FirebaseViewModelFactory

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }


    override fun onClick(p0: View?) {
        if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        } else if (p0 == binding.btnDaftar) {
            register()
        }
    }

    private fun setView() {
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnDaftar.setOnClickListener(this)
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.pendaftaran_mahasiswa)
        userViewModel =
            ViewModelProvider(viewModelStore, FirebaseViewModelFactory(this@RegisterActivity)).get(
                UserViewModel::class.java
            )
    }

    private fun register() {
        val nim = binding.tvNim.text.toString().trim()
        val namaDepan = binding.tvNamaDepan.text.toString().trim()
        val namaBelakang = binding.tvNamaBelakang.text.toString().trim()
        val programStudi = binding.spinProdi.text.toString().trim()
        val email = binding.tvEmail.text.toString().trim()
        val password = binding.tvPassword.text.toString().trim()

        when {
            nim.isEmpty() -> {
                binding.tvNim.error = resources.getString(R.string.nimerr)
                binding.tvNim.requestFocus()
            }
            namaDepan.isEmpty() -> {
                binding.tvNamaDepan.error = resources.getString(R.string.namadepanerr)
                binding.tvNamaDepan.requestFocus()
            }
            namaBelakang.isEmpty() -> {
                binding.tvNamaBelakang.error = resources.getString(R.string.namabelakangerr)
                binding.tvNamaBelakang.requestFocus()
            }
            programStudi.isEmpty() -> {
                binding.spinProdi.error = resources.getString(R.string.prodierr)
                binding.spinProdi.requestFocus()
            }
            email.isEmpty() -> {
                binding.tvEmail.error = resources.getString(R.string.emailerr)
                binding.tvEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.tvPassword.error = resources.getString(R.string.passerr)
                binding.tvPassword.requestFocus()
            }
            else -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.btnDaftar.windowToken, 0)

                binding.layoutContent.visibility = View.GONE
                binding.includeProgressBar.root.visibility = View.VISIBLE

                val popupMessageUserBinding =
                    PopupMessageUserBinding.inflate(LayoutInflater.from(this@RegisterActivity))
                val alertbuilder = AlertDialog.Builder(this@RegisterActivity)
                alertbuilder.setView(popupMessageUserBinding.root)
                val alertDialog = alertbuilder.create()

                userViewModel.getMaxId().observe(this) {
                    val user = User(
                        (it + 1).toString(),
                        nim,
                        namaDepan,
                        namaBelakang,
                        programStudi,
                        email,
                        password,
                        ""
                    )
                    userViewModel.user = user

                    userViewModel.checkNim().observe(this) { statusNim ->
                        when (statusNim) {
                            0 -> {
                                userViewModel.registerUser().observe(this) { isSuccess ->
                                    binding.layoutContent.visibility = View.VISIBLE
                                    binding.includeProgressBar.root.visibility = View.GONE
                                    alertDialog.show()

                                    if (isSuccess) {
                                        popupMessageUserBinding.tvMessage.text =
                                            resources.getString(R.string.berhasilregister)
                                        popupMessageUserBinding.btnOke.setOnClickListener {
                                            finish()
                                        }
                                    } else {
                                        popupMessageUserBinding.tvMessage.text =
                                            resources.getString(R.string.gagalregister)
                                        popupMessageUserBinding.btnOke.setOnClickListener {
                                            alertDialog.dismiss()
                                        }
                                        binding.tvNamaBelakang.text = null
                                        binding.tvNamaDepan.text = null
                                        binding.tvEmail.text = null
                                        binding.tvPassword.text = null
                                    }
                                }
                            }

                            1 -> {
                                binding.layoutContent.visibility = View.VISIBLE
                                binding.includeProgressBar.root.visibility = View.GONE

                                alertDialog.show()
                                popupMessageUserBinding.tvMessage.text =
                                    resources.getString(R.string.emailterdaftar)
                                popupMessageUserBinding.btnOke.setOnClickListener {
                                    alertDialog.dismiss()
                                }
                            }

                            else -> {
                                binding.layoutContent.visibility = View.VISIBLE
                                binding.includeProgressBar.root.visibility = View.GONE

                                alertDialog.show()
                                popupMessageUserBinding.tvMessage.text =
                                    resources.getString(R.string.errorregister)
                                popupMessageUserBinding.btnOke.setOnClickListener {
                                    alertDialog.dismiss()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}