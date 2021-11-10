package com.user.bemp.view.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.user.bemp.R
import com.user.bemp.databinding.ActivityPrestasiBinding
import com.user.bemp.helper.TB_PRESTASI
import com.user.bemp.model.Prestasi
import com.user.bemp.view.adapter.PrestasiAdapter
import com.user.bemp.viewmodel.FirebaseViewModelFactory
import com.user.bemp.viewmodel.PrestasiViewModel
import java.util.ArrayList

class PrestasiActivity : AppCompatActivity(), View.OnClickListener,
    TextView.OnEditorActionListener {
    private lateinit var binding: ActivityPrestasiBinding
    private lateinit var prestasiViewModel: PrestasiViewModel
    private lateinit var prestasiAdapter: PrestasiAdapter

    private var search = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrestasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.includeToolbar.imgBtnBack){
            finish()
        } else if (p0 == binding.imgSearch){
            search = binding.edtSearch.text.toString()
            if (search == ""){
                showPrestasi()
            } else {
                searchPrestasi()
            }
        }
    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == EditorInfo.IME_ACTION_SEARCH) {
            search = binding.edtSearch.text.toString()
            if (search == ""){
                showPrestasi()
            } else {
                searchPrestasi()
            }
        }
        return true
    }

    private fun setView() {
        prestasiViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@PrestasiActivity)
        ).get(PrestasiViewModel::class.java)

        prestasiAdapter = PrestasiAdapter(this@PrestasiActivity)
        binding.rvPrestasi.layoutManager = LinearLayoutManager(this@PrestasiActivity)
        binding.rvPrestasi.setHasFixedSize(true)
        binding.includeToolbar.tvTitle.text = resources.getString(R.string.prestasi_mahasiswa)
        binding.includeToolbar.imgBtnBack.setOnClickListener(this)
        binding.imgSearch.setOnClickListener(this)
        binding.edtSearch.setOnEditorActionListener(this)

        showPrestasi()
    }

    private fun showPrestasi(){
        binding.rvPrestasi.visibility = View.GONE
        binding.includeToolbar.root.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE

        prestasiViewModel.showPrestasi().observe(this){ hashMap ->
            when (hashMap["code"]) {
                200 -> {
                    prestasiAdapter.listPrestasi = hashMap[TB_PRESTASI] as ArrayList<Prestasi>
                    binding.rvPrestasi.adapter = prestasiAdapter
                }
                403 -> {
                    Toast.makeText(
                        this@PrestasiActivity,
                        resources.getString(R.string.errorload),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                404 -> {
                    prestasiAdapter.listPrestasi = ArrayList()
                    binding.rvPrestasi.adapter = prestasiAdapter
                }
            }
            binding.rvPrestasi.visibility = View.VISIBLE
            binding.includeToolbar.root.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }

    private fun searchPrestasi(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)

        binding.rvPrestasi.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE
        prestasiViewModel.search = search
        prestasiViewModel.searchPrestasi().observe(this){ hashMap ->
            when (hashMap["code"]) {
                200 -> {
                    prestasiAdapter.listPrestasi = ArrayList()
                    prestasiAdapter.listPrestasi = hashMap[TB_PRESTASI] as ArrayList<Prestasi>
                    binding.rvPrestasi.adapter = prestasiAdapter
                }
                403 -> {
                    Toast.makeText(
                        this@PrestasiActivity,
                        resources.getString(R.string.errorload),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                404 -> {
                    prestasiAdapter.listPrestasi = ArrayList()
                    binding.rvPrestasi.adapter = prestasiAdapter
                }
            }
            binding.rvPrestasi.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }
}