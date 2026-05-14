package com.example.reddoorz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Your XML name

        // Use a Handler to delay the transition
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the Login Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // "Finish" the Splash so the user can't go back to it with the back button
            finish()
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}