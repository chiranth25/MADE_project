package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.made_project.R
import com.example.made_project.utils.SharedPrefManager
import com.example.made_project.utils.UiAnimator
import com.google.android.material.button.MaterialButton

class ProfileActivity : BaseActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPrefManager = SharedPrefManager(this)

        findViewById<TextView>(R.id.textProfileName).text = sharedPrefManager.getUserName()
        findViewById<TextView>(R.id.textProfileEmail).text = sharedPrefManager.getUserEmail()
        findViewById<TextView>(R.id.textAppVersion).text = getString(R.string.app_version)
        findViewById<ImageView>(R.id.imageProfile).setImageResource(R.drawable.ic_profile_placeholder)
        UiAnimator.animateSequence(
            findViewById(R.id.cardProfileHeader),
            findViewById(R.id.cardProfileSettings),
            findViewById<View>(R.id.buttonLogout),
            startDelay = 80L
        )
        UiAnimator.pop(findViewById(R.id.imageProfile), 220L)

        val darkModeSwitch = findViewById<Switch>(R.id.switchDarkMode)
        darkModeSwitch.isChecked = sharedPrefManager.isDarkModeEnabled()
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefManager.saveDarkMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        findViewById<MaterialButton>(R.id.buttonLogout).setOnClickListener {
            sharedPrefManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        setupBottomNavigation(findViewById(R.id.bottomNavigationProfile), R.id.nav_profile)
    }
}
