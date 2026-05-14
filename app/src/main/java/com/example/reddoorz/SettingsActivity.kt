package com.example.reddoorz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables edge-to-edge display to allow content to flow behind system bars
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Handle window insets for the Settings screen.
        // This ensures the header and the logout button aren't covered by the 
        // Status bar or the System Navigation bar.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply padding to keep content within the safe viewing area
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the Logout button
        val btnLogout = findViewById<AppCompatButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Sign out the user from Firebase
            FirebaseAuth.getInstance().signOut()
            
            // Redirect back to the LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            
            // Clear the activity stack so the user cannot press "back" to return to settings
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            
            // Close the SettingsActivity
            finish()
        }
    }
}