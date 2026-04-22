package com.example.made_project.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class SharedPrefManager(context: Context) {

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSignupDetails(name: String, email: String, password: String) {
        preferences.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun validateLogin(email: String, password: String): Boolean {
        val savedEmail = preferences.getString(KEY_EMAIL, "") ?: ""
        val savedPassword = preferences.getString(KEY_PASSWORD, "") ?: ""
        return email == savedEmail && password == savedPassword
    }

    fun createSession() {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
    }

    fun clearSession() {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun saveOnboardingSeen() {
        preferences.edit().putBoolean(KEY_INTRO_SEEN, true).apply()
    }

    fun isOnboardingSeen(): Boolean = preferences.getBoolean(KEY_INTRO_SEEN, false)

    fun getUserName(): String = preferences.getString(KEY_NAME, "Student") ?: "Student"

    fun getUserEmail(): String = preferences.getString(KEY_EMAIL, "student@example.com") ?: "student@example.com"

    fun saveDarkMode(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean = preferences.getBoolean(KEY_DARK_MODE, false)

    fun applySavedTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        private const val PREF_NAME = "task_manager_prefs"
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PASSWORD = "user_password"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_INTRO_SEEN = "intro_seen"
        private const val KEY_DARK_MODE = "dark_mode"
    }
}
