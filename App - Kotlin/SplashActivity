package com.example.albergo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Controlla subito lo stato di login
        val isLoggedIn = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false)

        // Naviga alla schermata corretta appena l'animazione è terminata
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (isLoggedIn) {
                Intent(this, DashboardActivity::class.java) // Se loggato, vai alla dashboard
            } else {
                Intent(this, LoginActivity::class.java)     // Se non loggato, vai alla login
            }
            startActivity(intent)
            finish()
        }, 3000) // Attendi solo il tempo necessario all'animazione
    }
}
