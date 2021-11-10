package com.admin.bempadmin.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityPrestasiBinding
import com.admin.bempadmin.databinding.PopupAlertAdminBinding
import com.admin.bempadmin.helper.SHARED_PREFERENCES
import com.admin.bempadmin.helper.STATUS_ADMIN
import com.admin.bempadmin.helper.TB_PRESTASI
import com.admin.bempadmin.helper.UPDATE
import com.admin.bempadmin.model.Prestasi
import com.admin.bempadmin.view.adapter.PrestasiAdapter
import com.admin.bempadmin.view.utils.OnClickPrestasiEventListener
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.PrestasiViewModel
import com.google.android.material.navigation.NavigationBarView
import java.util.ArrayList

class PrestasiActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    View.OnClickListener, OnClickPrestasiEventListener, TextView.OnEditorActionListener {
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
        if (p0 == binding.fab) {
            startActivity(Intent(this@PrestasiActivity, TambahPrestasiActivity::class.java))
        } else if (p0 == binding.imgSearch){
            search = binding.edtSearch.text.toString()
            if (search == ""){
                showPrestasi()
            } else {
                searchPrestasi()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_berita -> {
                intent = Intent(this@PrestasiActivity, BeritaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_lomba -> {
                intent = Intent(this@PrestasiActivity, LombaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_profil -> {
                intent = Intent(this@PrestasiActivity, ProfilActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            else -> return false
        }
        return true
    }

    override fun updatePrestasi(prestasi: Prestasi) {
        val intent = Intent(this@PrestasiActivity, TambahPrestasiActivity::class.java)
        intent.putExtra(TB_PRESTASI, prestasi)
        intent.putExtra(UPDATE, UPDATE)
        startActivity(intent)
    }

    override fun deletePrestasi(prestasi: Prestasi) {
        val popupAlertAdminBinding =
            PopupAlertAdminBinding.inflate(LayoutInflater.from(this@PrestasiActivity))
        val alertbuilder = AlertDialog.Builder(this@PrestasiActivity)
        alertbuilder.setView(popupAlertAdminBinding.root)
        alertbuilder.setCancelable(true)
        val alertDialog = alertbuilder.create()
        alertDialog.show()

        popupAlertAdminBinding.tvMessage.text =
            resources.getString(R.string.warninghapusprestasi)
        popupAlertAdminBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        popupAlertAdminBinding.btnOke.setOnClickListener {
            binding.rvPrestasi.visibility = View.GONE
            binding.includeProgressBar.root.visibility = View.VISIBLE

            prestasiViewModel.prestasi = prestasi
            prestasiViewModel.delete().observe(this) {
                alertDialog.dismiss()
                if (it) {
                    Toast.makeText(
                        this@PrestasiActivity,
                        "Berhasil di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this@PrestasiActivity,
                        "Gagal di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                binding.rvPrestasi.visibility = View.VISIBLE
                binding.includeProgressBar.root.visibility = View.GONE
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
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val superAdmin = sharedPreferences.getString(STATUS_ADMIN, "").toString()

        prestasiViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@PrestasiActivity)
        ).get(PrestasiViewModel::class.java)

        binding.bottomNav.menu.findItem(R.id.menu_prestasi).isChecked = true
        binding.bottomNav.setOnItemSelectedListener(this)
        binding.fab.setOnClickListener(this)
        binding.imgSearch.setOnClickListener(this)
        binding.edtSearch.setOnEditorActionListener(this)

        prestasiAdapter = PrestasiAdapter(this@PrestasiActivity, this, superAdmin)
        binding.rvPrestasi.layoutManager = LinearLayoutManager(this@PrestasiActivity)
        binding.rvPrestasi.setHasFixedSize(true)
        showPrestasi()
    }

    private fun showPrestasi() {
        binding.rvPrestasi.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE
        prestasiViewModel.showPrestasi().observe(this) { hashMap ->
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