package com.admin.bempadmin.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.admin.bempadmin.R
import com.admin.bempadmin.databinding.ActivityLombaBinding
import com.admin.bempadmin.databinding.PopupAlertAdminBinding
import com.admin.bempadmin.helper.SHARED_PREFERENCES
import com.admin.bempadmin.helper.STATUS_ADMIN
import com.admin.bempadmin.helper.TB_LOMBA
import com.admin.bempadmin.helper.UPDATE
import com.admin.bempadmin.model.Lomba
import com.admin.bempadmin.view.adapter.LombaAdapter
import com.admin.bempadmin.view.utils.OnClickLombaEventListener
import com.admin.bempadmin.viewmodel.FirebaseViewModelFactory
import com.admin.bempadmin.viewmodel.LombaViewModel
import com.google.android.material.navigation.NavigationBarView
import java.util.ArrayList

class LombaActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    View.OnClickListener, OnClickLombaEventListener {
    private lateinit var binding: ActivityLombaBinding
    private lateinit var lombaViewModel: LombaViewModel
    private lateinit var lombaAdapter: LombaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLombaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.fab) {
            startActivity(Intent(this@LombaActivity, TambahLombaActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_berita -> {
                intent = Intent(this@LombaActivity, BeritaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_prestasi -> {
                intent = Intent(this@LombaActivity, PrestasiActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.menu_profil -> {
                intent = Intent(this@LombaActivity, ProfilActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            else -> return false
        }
        return true
    }

    override fun updateLomba(lomba: Lomba) {
        val intent = Intent(this@LombaActivity, TambahLombaActivity::class.java)
        intent.putExtra(TB_LOMBA, lomba)
        intent.putExtra(UPDATE, UPDATE)
        startActivity(intent)
    }

    override fun deleteLomba(lomba: Lomba) {
        val popupAlertAdminBinding =
            PopupAlertAdminBinding.inflate(LayoutInflater.from(this@LombaActivity))
        val alertbuilder = AlertDialog.Builder(this@LombaActivity)
        alertbuilder.setView(popupAlertAdminBinding.root)
        alertbuilder.setCancelable(true)
        val alertDialog = alertbuilder.create()
        alertDialog.show()

        popupAlertAdminBinding.tvMessage.text =
            resources.getString(R.string.warninghapuslomba)
        popupAlertAdminBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        popupAlertAdminBinding.btnOke.setOnClickListener {
            binding.rvLomba.visibility = View.GONE
            binding.includeProgressBar.root.visibility = View.VISIBLE

            lombaViewModel.lomba = lomba
            lombaViewModel.delete().observe(this) {
                alertDialog.dismiss()
                if (it) {
                    Toast.makeText(
                        this@LombaActivity,
                        "Berhasil di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this@LombaActivity,
                        "Gagal di hapus",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                binding.rvLomba.visibility = View.VISIBLE
                binding.includeProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun setView() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val superAdmin = sharedPreferences.getString(STATUS_ADMIN, "").toString()

        lombaViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@LombaActivity)
        ).get(LombaViewModel::class.java)
        binding.bottomNav.menu.findItem(R.id.menu_lomba).isChecked = true
        binding.bottomNav.setOnItemSelectedListener(this)

        binding.fab.setOnClickListener(this)

        lombaAdapter = LombaAdapter(this@LombaActivity,this, superAdmin)
        binding.rvLomba.layoutManager = LinearLayoutManager(this@LombaActivity)
        binding.rvLomba.setHasFixedSize(true)
        showLomba()
    }

    private fun showLomba() {
        binding.rvLomba.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE

        lombaViewModel.showLomba().observe(this) { hashMap ->
            when (hashMap["code"]) {
                200 -> {
                    lombaAdapter.listLomba = hashMap[TB_LOMBA] as ArrayList<Lomba>
                    binding.rvLomba.adapter = lombaAdapter
                }
                403 -> {
                    Toast.makeText(
                        this@LombaActivity,
                        resources.getString(R.string.errorload),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                404 -> {
                    lombaAdapter.listLomba = ArrayList()
                    binding.rvLomba.adapter = lombaAdapter
                }
            }
            binding.rvLomba.visibility = View.VISIBLE
            binding.includeProgressBar.root.visibility = View.GONE
        }
    }

}