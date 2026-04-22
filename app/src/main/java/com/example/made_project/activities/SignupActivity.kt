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

class SignupActivity : AppCompatActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sharedPrefManager = SharedPrefManager(this)
        UiAnimator.animateSequence(findViewById<View>(R.id.layoutSignupContent), startDelay = 100L)

        val nameInput = findViewById<TextInputEditText>(R.id.editSignupName)
        val emailInput = findViewById<TextInputEditText>(R.id.editSignupEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.editSignupPassword)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.editConfirmPassword)

        findViewById<MaterialButton>(R.id.buttonSignup).setOnClickListener {
            val name = nameInput.text?.toString()?.trim().orEmpty()
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val password = passwordInput.text?.toString()?.trim().orEmpty()
            val confirmPassword = confirmPasswordInput.text?.toString()?.trim().orEmpty()

            when {
                name.isEmpty() -> nameInput.error = "Full name is required"
                email.isEmpty() -> emailInput.error = "Email is required"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> emailInput.error = "Enter valid email"
                password.length < 6 -> passwordInput.error = "Minimum 6 characters"
                confirmPassword != password -> confirmPasswordInput.error = "Passwords do not match"
                else -> {
                    sharedPrefManager.saveSignupDetails(name, email, password)
                    Toast.makeText(this, "Signup successful. Please login.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        findViewById<TextView>(R.id.textGoToLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
