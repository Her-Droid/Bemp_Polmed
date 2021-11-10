package com.user.bemp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.user.bemp.R
import com.user.bemp.databinding.ActivityStrukturOrganisasiBinding
import com.user.bemp.helper.ANGGOTA
import com.user.bemp.model.StrukturOrganisasi
import com.user.bemp.view.adapter.StrukturOrganisasiAdapter
import com.user.bemp.viewmodel.FirebaseViewModelFactory
import com.user.bemp.viewmodel.StrukturOrganisasiViewModel

class StrukturOrganisasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStrukturOrganisasiBinding
    private lateinit var strukturOrganisasiAdapter: StrukturOrganisasiAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStrukturOrganisasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.struktur_organisasi)
        binding.includeToolbar.imgBtnBack.setOnClickListener { finish() }
        binding.rvStrukturOrganisasi.layoutManager =
            LinearLayoutManager(this@StrukturOrganisasiActivity)
        binding.rvStrukturOrganisasi.isNestedScrollingEnabled = false
        binding.rvStrukturOrganisasi.setHasFixedSize(false)
        strukturOrganisasiAdapter = StrukturOrganisasiAdapter(this@StrukturOrganisasiActivity)

        loadStrukturOrganisasi()
    }

    private fun loadStrukturOrganisasi() {
        binding.scrollView.visibility = View.GONE
        binding.includeToolbar.root.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE

        val strukturOrganisasiViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@StrukturOrganisasiActivity)
        ).get(StrukturOrganisasiViewModel::class.java)
        strukturOrganisasiViewModel.showBioStrukturOrgnisasi().observe(this) {
            binding.tvBio.text = it

            strukturOrganisasiViewModel.showAnggota().observe(this) { hashmap ->
                binding.scrollView.visibility = View.VISIBLE
                binding.includeToolbar.root.visibility = View.VISIBLE
                binding.includeProgressBar.root.visibility = View.GONE
                when (hashmap["code"]) {
                    200 -> {
                        strukturOrganisasiAdapter.listAnggota =
                            hashmap[ANGGOTA] as ArrayList<StrukturOrganisasi>
                        binding.rvStrukturOrganisasi.adapter = strukturOrganisasiAdapter
                    }
                    403 -> {
                        Toast.makeText(
                            this@StrukturOrganisasiActivity,
                            resources.getString(R.string.errorload),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    404 -> {
                        Log.v("jajal", "masuk")
                        strukturOrganisasiAdapter.listAnggota = ArrayList()
                        binding.rvStrukturOrganisasi.adapter = strukturOrganisasiAdapter
                    }
                }
            }
        }
    }

}