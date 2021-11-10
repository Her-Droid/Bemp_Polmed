package com.admin.bempadmin.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityDetailBeritaBinding
import com.admin.bempadmin.helper.ISIBERITA
import com.admin.bempadmin.model.Berita
import com.admin.bempadmin.model.BeritaModel
import com.bumptech.glide.Glide

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
        val berita = intent.getParcelableExtra<BeritaModel>(ISIBERITA)
        if (berita != null) {
            binding.tvTanggalPosting.text = berita.date
            binding.tvJudulBerita.text = berita.judul
            binding.tvKategori.text = berita.idKategori
            binding.tvDipostingOleh.text = berita.namaAdmin
            if (berita.namaAdminEdit == "-") {
                binding.layoutEdit.visibility = View.GONE
            } else {
                binding.layoutEdit.visibility = View.VISIBLE
                binding.tvDieditOleh.text = berita.namaAdminEdit
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