package com.user.bemp.view.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.user.bemp.R
import com.user.bemp.databinding.ActivityDetailLombaBinding
import com.user.bemp.databinding.PopupMessageUserBinding
import com.user.bemp.helper.CAMPAIGN
import com.user.bemp.helper.ID_USER
import com.user.bemp.helper.NAMA_USER
import com.user.bemp.helper.SHARED_PREFERENCES
import com.user.bemp.model.DaftarLomba
import com.user.bemp.model.Lomba
import com.user.bemp.viewmodel.FirebaseViewModelFactory
import com.user.bemp.viewmodel.LombaViewModel

class DetailLombaActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailLombaBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var lombaViewModel: LombaViewModel
    private lateinit var lomba: Lomba

    private var idLomba = ""
    private var idUser = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLombaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        } else if (p0 == binding.btnIkutLomba) {
            ikutLomba()
        }
    }

    private fun setView() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        idUser = sharedPreferences.getString(ID_USER, "").toString()

        lombaViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@DetailLombaActivity)
        ).get(LombaViewModel::class.java)

        binding.includeToolbar.tvTitle.text = resources.getString(R.string.detail_lomba)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.btnIkutLomba.setOnClickListener(this)

        val lomba = intent.getParcelableExtra<Lomba>(CAMPAIGN)
        if (lomba != null) {
            this.lomba = lomba
            binding.tvJudulLomba.text = lomba.judul
            binding.tvDeskripsi.text = lomba.deskripsi
            Glide.with(this@DetailLombaActivity)
                .load(lomba.imageUrl)
                .into(binding.imgFoto)
            idLomba = lomba.id
        }
        checkPendaftaran()
    }

    private fun checkPendaftaran() {
        binding.includeProgressBar.root.visibility = View.VISIBLE
        binding.includeToolbar.root.visibility = View.GONE
        binding.layoutContent.visibility = View.GONE
        binding.btnIkutLomba.visibility = View.GONE
        lombaViewModel.daftarLomba = DaftarLomba(idUser = idUser, idLomba = idLomba)
        lombaViewModel.checkDaftarLomba().observe(this) { check ->
            if (check) {
                binding.btnIkutLomba.setBackgroundColor(
                    ContextCompat.getColor(
                        this@DetailLombaActivity,
                        R.color.colore0e0e0
                    )
                )
                binding.btnIkutLomba.setTextColor(
                    ContextCompat.getColor(
                        this@DetailLombaActivity,
                        R.color.colora1a1a1
                    )
                )
                binding.btnIkutLomba.text = resources.getString(R.string.sudahdaftar)
                binding.btnIkutLomba.setTypeface(binding.btnIkutLomba.typeface, Typeface.NORMAL)
                binding.btnIkutLomba.isEnabled = false
            } else {
                binding.btnIkutLomba.setBackgroundColor(
                    ContextCompat.getColor(
                        this@DetailLombaActivity,
                        R.color.colorc06eff
                    )
                )
                binding.btnIkutLomba.setTextColor(
                    ContextCompat.getColor(
                        this@DetailLombaActivity,
                        R.color.white
                    )
                )
                binding.btnIkutLomba.text = resources.getString(R.string.ikut_lomba)
                binding.btnIkutLomba.setTypeface(binding.btnIkutLomba.typeface, Typeface.BOLD)
                binding.btnIkutLomba.isEnabled = true
            }

            binding.includeProgressBar.root.visibility = View.GONE
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.VISIBLE
            binding.btnIkutLomba.visibility = View.VISIBLE
        }
    }

    private fun ikutLomba() {
        binding.includeProgressBar.root.visibility = View.VISIBLE
        binding.includeToolbar.root.visibility = View.GONE
        binding.layoutContent.visibility = View.GONE
        binding.btnIkutLomba.visibility = View.GONE
        lombaViewModel.getMaxId().observe(this) {
            lombaViewModel.daftarLomba = DaftarLomba(
                (it + 1).toString(), idUser, idLomba
            )
            lombaViewModel.saveDaftarLomba().observe(this) { it1 ->
                val popupMessageUserBinding =
                    PopupMessageUserBinding.inflate(LayoutInflater.from(this@DetailLombaActivity))
                val alertbuilder = AlertDialog.Builder(this@DetailLombaActivity)
                alertbuilder.setView(popupMessageUserBinding.root)
                val alertDialog = alertbuilder.create()
                alertDialog.show()

                if (it1) {
                    popupMessageUserBinding.tvMessage.text =
                        resources.getString(
                            R.string.berhasildaftar, sharedPreferences.getString(
                                NAMA_USER, ""
                            ).toString()
                        )
                } else {
                    popupMessageUserBinding.tvMessage.text =
                        resources.getString(R.string.gagaldaftarlomba)
                }
                popupMessageUserBinding.btnOke.setOnClickListener {
                    alertDialog.dismiss()
                }
                binding.includeProgressBar.root.visibility = View.GONE
                binding.includeToolbar.root.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.VISIBLE
                binding.btnIkutLomba.visibility = View.VISIBLE
                binding.btnIkutLomba.setBackgroundColor(
                    ContextCompat.getColor(
                        this@DetailLombaActivity,
                        R.color.colore0e0e0
                    )
                )
                binding.btnIkutLomba.setTextColor(
                    ContextCompat.getColor(
                        this@DetailLombaActivity,
                        R.color.colora1a1a1
                    )
                )
                binding.btnIkutLomba.text = resources.getString(R.string.sudahdaftar)
                binding.btnIkutLomba.setTypeface(binding.btnIkutLomba.typeface, Typeface.NORMAL)
                binding.btnIkutLomba.isEnabled = false
            }
        }


    }
}