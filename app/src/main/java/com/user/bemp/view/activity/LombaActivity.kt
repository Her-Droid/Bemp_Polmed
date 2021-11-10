package com.user.bemp.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationBarView
import com.user.bemp.R
import com.user.bemp.databinding.ActivityLombaBinding
import com.user.bemp.helper.TB_LOMBA
import com.user.bemp.model.Lomba
import com.user.bemp.view.adapter.LombaAdapter
import com.user.bemp.viewmodel.FirebaseViewModelFactory
import com.user.bemp.viewmodel.LombaViewModel
import java.util.ArrayList

class LombaActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding: ActivityLombaBinding
    private lateinit var lombaAdapter: LombaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLombaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                intent = Intent(this@LombaActivity, HomeActivity::class.java)
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

    private fun setView(){
        binding.bottomNav.menu.findItem(R.id.menu_lomba).isChecked = true
        binding.bottomNav.setOnItemSelectedListener(this)

        lombaAdapter = LombaAdapter(this@LombaActivity)
        binding.rvLomba.layoutManager = LinearLayoutManager(this@LombaActivity)
        binding.rvLomba.setHasFixedSize(true)
        showLomba()
    }

    private fun showLomba() {
        binding.rvLomba.visibility = View.GONE
        binding.includeProgressBar.root.visibility = View.VISIBLE
        val lombaViewModel = ViewModelProvider(
            viewModelStore,
            FirebaseViewModelFactory(this@LombaActivity)
        ).get(LombaViewModel::class.java)
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