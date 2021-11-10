package com.user.bemp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.user.bemp.R
import com.user.bemp.databinding.ActivityDetailBeritaBinding
import com.user.bemp.helper.ISIBERITA
import com.user.bemp.model.Berita

class DetailBeritaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBeritaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        binding.includeToolbar.imgBtnBack.setOnClickListener { finish() }
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.berita)
        showDetail()
    }

    private fun showDetail() {
        val berita = intent.getParcelableExtra<Berita>(ISIBERITA)
        if (berita != null) {
            binding.tvTanggalPosting.text = berita.date
            binding.tvJudulBerita.text = berita.judul
            binding.tvKategori.text = berita.idKategori
            binding.tvDipostingOleh.text = berita.postingAdmin
            if (berita.postingUpdateAdmin == "-") {
                binding.layoutEdit.visibility = View.GONE
            } else {
                binding.layoutEdit.visibility = View.VISIBLE
                binding.tvDieditOleh.text = berita.postingUpdateAdmin
            }
            binding.tvIsiBerita.text = berita.isi
            if (berita.imageUrl == "null" || berita.imageUrl.isEmpty()) {
                binding.imgFoto.visibility = View.GONE
            } else {
                binding.imgFoto.visibility = View.VISIBLE
            }
            Glide.with(this@DetailBeritaActivity)
                .load(berita.imageUrl)
                .into(binding.imgFoto)
        }
    }
}