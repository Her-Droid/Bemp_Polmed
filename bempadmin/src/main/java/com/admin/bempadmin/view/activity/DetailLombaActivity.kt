package com.admin.bempadmin.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityDetailLombaBinding
import com.admin.bempadmin.helper.CAMPAIGN
import com.admin.bempadmin.helper.PENDAFTAR
import com.admin.bempadmin.model.Lomba
import com.admin.bempadmin.view.adapter.DaftarPesertaAdapter
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.LombaViewModel
import com.bumptech.glide.Glide

class DetailLombaActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailLombaBinding
    private lateinit var daftarPesertaAdapter: DaftarPesertaAdapter
    private lateinit var lomba: Lomba

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLombaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.includeToolbar.imgBtnBack) {
            finish()
        }
    }

    private fun setView() {
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.detail_lomba)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)

        daftarPesertaAdapter = DaftarPesertaAdapter(this@DetailLombaActivity)
        binding.rvDaftarPeserta.layoutManager = LinearLayoutManager(this@DetailLombaActivity)
        binding.rvDaftarPeserta.isNestedScrollingEnabled = false
        binding.rvDaftarPeserta.setHasFixedSize(false)
        val lomba = intent.getParcelableExtra<Lomba>(CAMPAIGN)
        if (lomba != null) {
            this.lomba = lomba
            binding.tvJudulLomba.text = lomba.judul
            binding.tvDeskripsi.text = lomba.deskripsi
            Glide.with(this@DetailLombaActivity)
                .load(lomba.imageUrl)
                .into(binding.imgFoto)
            showPeserta()
        } else {
            finish()
        }
    }

    private fun showPeserta() {
        binding.includeProgressBar.root.visibility = View.VISIBLE
        binding.includeToolbar.root.visibility = View.GONE
        binding.layoutContent.visibility = View.GONE

        val lombaViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@DetailLombaActivity)
        ).get(LombaViewModel::class.java)
        lombaViewModel.lomba = lomba
        lombaViewModel.showPeserta().observe(this) { hashMap ->
            if (hashMap["code"].toString().toInt() == 200) {
                val listPeserta = hashMap[PENDAFTAR] as ArrayList<String>
                daftarPesertaAdapter.listPeserta = listPeserta
                binding.rvDaftarPeserta.adapter = daftarPesertaAdapter
            }
            binding.includeProgressBar.root.visibility = View.GONE
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.VISIBLE
        }
    }
}