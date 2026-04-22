package com.example.made_project.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.made_project.R
import com.example.made_project.utils.SharedPrefManager
import com.example.made_project.utils.UiAnimator

class IntroActivity : AppCompatActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var titleText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var buttonNext: Button
    private var currentIndex = 0

    private val slides = listOf(
        "Organize tasks easily" to "Create tasks, set due dates, and keep work structured every day.",
        "Use priority matrix" to "Place each task inside the Eisenhower Matrix to know what matters now.",
        "Improve productivity" to "Track completion, search tasks, and focus on the right work."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        sharedPrefManager = SharedPrefManager(this)
        titleText = findViewById(R.id.textIntroTitle)
        descriptionText = findViewById(R.id.textIntroDescription)
        buttonNext = findViewById(R.id.buttonNextIntro)
        UiAnimator.pop(findViewById<ImageView>(R.id.imageIntroLogo), 80L)
        UiAnimator.animateSequence(
            titleText,
            descriptionText,
            findViewById<LinearLayout>(R.id.layoutIntroActions),
            startDelay = 140L
        )

        findViewById<Button>(R.id.buttonSkipIntro).setOnClickListener {
            finishIntro()
        }

        buttonNext.setOnClickListener {
            if (currentIndex < slides.lastIndex) {
                currentIndex++
                bindSlide()
            } else {
                finishIntro()
            }
        }

        bindSlide()
    }

    private fun bindSlide() {
        titleText.text = slides[currentIndex].first
        descriptionText.text = slides[currentIndex].second
        buttonNext.text = if (currentIndex == slides.lastIndex) "Get Started" else "Next"
        UiAnimator.animateSequence(titleText, descriptionText, startDelay = 0L, stepDelay = 70L)
    }

    private fun finishIntro() {
        sharedPrefManager.saveOnboardingSeen()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
