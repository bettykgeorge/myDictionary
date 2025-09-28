package com.example.mydictionary.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mydictionary.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        val appNameTextView = findViewById<TextView>(R.id.appNameTextView)
        val taglineTextView = findViewById<TextView>(R.id.taglineTextView)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)

        // Apply animations
        logoImageView.startAnimation(fadeIn)
        appNameTextView.startAnimation(slideUp)
        taglineTextView.startAnimation(slideUp)

        // Navigate to LoginActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000) // 3 seconds delay
    }
} 