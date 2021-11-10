package com.user.bemp.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.user.bemp.databinding.ActivityLandingPageBinding
import com.user.bemp.helper.SESSION
import com.user.bemp.helper.SHARED_PREFERENCES
import kotlin.concurrent.thread

class LandingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashscreen()
    }

    private fun splashscreen(){
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val session = sharedPreferences.getBoolean(SESSION, false)
        thread {
            Thread.sleep(3000)
            if (session) {
                startActivity(Intent(this@LandingPageActivity, HomeActivity::class.java))
            } else {
                startActivity(Intent(this@LandingPageActivity, LoginActivity::class.java))
            }
            finish()
        }
    }
}