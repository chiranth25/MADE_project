package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.made_project.R
import com.example.made_project.utils.SharedPrefManager
import com.example.made_project.utils.UiAnimator

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefManager = SharedPrefManager(this)
        sharedPrefManager.applySavedTheme()
        setContentView(R.layout.activity_splash)
        UiAnimator.pop(findViewById<ImageView>(R.id.imageSplashLogo))
        UiAnimator.animateSequence(
            findViewById<TextView>(R.id.textSplashTitle),
            findViewById<TextView>(R.id.textSplashTagline),
            startDelay = 120L
        )
        UiAnimator.pulse(findViewById(R.id.imageSplashLogo))

        Handler(Looper.getMainLooper()).postDelayed({
            val destination = when {
                !sharedPrefManager.isOnboardingSeen() -> IntroActivity::class.java
                sharedPrefManager.isLoggedIn() -> DashboardActivity::class.java
                else -> LoginActivity::class.java
            }
            startActivity(Intent(this, destination))
            finish()
        }, 2200)
    }
}
