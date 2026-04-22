package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.made_project.R
import com.example.made_project.utils.SharedPrefManager
import com.example.made_project.utils.UiAnimator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPrefManager = SharedPrefManager(this)
        emailInput = findViewById(R.id.editLoginEmail)
        passwordInput = findViewById(R.id.editLoginPassword)
        UiAnimator.animateSequence(findViewById<View>(R.id.cardLogin), startDelay = 120L)

        findViewById<MaterialButton>(R.id.buttonLogin).setOnClickListener {
            loginUser()
        }

        findViewById<TextView>(R.id.textGoToSignup).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = emailInput.text?.toString()?.trim().orEmpty()
        val password = passwordInput.text?.toString()?.trim().orEmpty()

        when {
            email.isEmpty() -> emailInput.error = "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> emailInput.error = "Enter valid email"
            password.isEmpty() -> passwordInput.error = "Password is required"
            sharedPrefManager.validateLogin(email, password) -> {
                sharedPrefManager.createSession()
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }

            else -> Toast.makeText(this, "Invalid credentials. Please signup first.", Toast.LENGTH_LONG).show()
        }
    }
}
