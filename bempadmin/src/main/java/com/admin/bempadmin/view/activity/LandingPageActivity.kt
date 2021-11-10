package com.admin.bempadmin.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.admin.bempadmin.databinding.ActivityLandingPageBinding
import com.admin.bempadmin.helper.SESSION
import com.admin.bempadmin.helper.SHARED_PREFERENCES
import kotlin.concurrent.thread

class LandingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashscreen()
    }

    private fun splashscreen() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val session = sharedPreferences.getBoolean(SESSION, false)
        thread {
            Thread.sleep(3000)
            if (session) {
                startActivity(Intent(this@LandingPageActivity, BeritaActivity::class.java))
            } else {
                startActivity(Intent(this@LandingPageActivity, LoginActivity::class.java))
            }
            finish()
        }
    }
}