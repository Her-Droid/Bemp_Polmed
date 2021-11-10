package com.user.bemp.view.activity

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.user.bemp.R
import com.user.bemp.databinding.ActivityTentangAplikasiBinding

class TentangAplikasiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTentangAplikasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTentangAplikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        binding.includeToolbar.tvTitle.text = resources.getText(R.string.tentang_aplikasi)
        binding.includeToolbar.imgBtnBack.setOnClickListener { finish() }
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            binding.tvVersiName.text = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}