package com.admin.bempadmin.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityEditBioStrukturOrganisasiBinding
import com.admin.bempadmin.databinding.PopupMessageAdminBinding
import com.admin.bempadmin.helper.BIO
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.BioOrganisasiViewModel

class EditBioStrukturOrganisasiActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {
    private lateinit var binding: ActivityEditBioStrukturOrganisasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBioStrukturOrganisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.btnSimpan) {
            saveBio()
        } else if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        binding.tvSisaText.text = (300 - p3).toString()
        binding.tvSisaText.text = (binding.tvSisaText.text.toString().toInt() - p1).toString()
    }

    override fun afterTextChanged(p0: Editable?) {}

    private fun setView() {
        binding.btnSimpan.setOnClickListener(this)
        val bio = intent.getStringExtra(BIO).toString()
        binding.edtBio.setText(bio)
        if (bio.isEmpty()) {
            binding.tvSisaText.text = resources.getString(R.string._300)
        } else {
            binding.tvSisaText.text = (300 - bio.length).toString()
        }
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.struktur_organisasi)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.edtBio.addTextChangedListener(this)
    }

    private fun saveBio() {
        val bio = binding.edtBio.text.toString().trim()
        if (bio.isEmpty()) {
            binding.edtBio.error = resources.getString(R.string.bioerr)
            binding.edtBio.requestFocus()
        } else {
            binding.includeProgressBar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.GONE
            val strukturOrganisasiViewModel = ViewModelProvider(
                viewModelStore,
                FirebaseViewModelFactory(this@EditBioStrukturOrganisasiActivity)
            ).get(BioOrganisasiViewModel::class.java)
            strukturOrganisasiViewModel.saveBio(bio).observe(this) {
                binding.includeProgressBar.root.visibility = View.GONE
                binding.layoutContent.visibility = View.VISIBLE

                val popupMessageAdminBinding =
                    PopupMessageAdminBinding.inflate(LayoutInflater.from(this@EditBioStrukturOrganisasiActivity))
                val alertbuilder = AlertDialog.Builder(this@EditBioStrukturOrganisasiActivity)
                alertbuilder.setView(popupMessageAdminBinding.root)
                val alertDialog = alertbuilder.create()
                alertDialog.show()
                if (it == 1) {
                    popupMessageAdminBinding.tvMessage.text =
                        resources.getString(R.string.biodisimpan)
                    popupMessageAdminBinding.btnOke.setOnClickListener {
                        alertDialog.dismiss()
                    }
                } else {
                    popupMessageAdminBinding.tvMessage.text =
                        resources.getString(R.string.biogagalsimpan)
                    popupMessageAdminBinding.btnOke.setOnClickListener {
                        alertDialog.dismiss()
                    }
                }
            }
        }
    }

}