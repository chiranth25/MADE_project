package com.example.made_project.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar

object UiAnimator {

    fun animateSequence(vararg views: View, startDelay: Long = 0L, stepDelay: Long = 90L) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 48f
            view.scaleX = 0.98f
            view.scaleY = 0.98f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(420L)
                .setStartDelay(startDelay + (index * stepDelay))
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    fun pop(view: View, delay: Long = 0L) {
        view.alpha = 0f
        view.scaleX = 0.82f
        view.scaleY = 0.82f
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(380L)
            .setStartDelay(delay)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun pulse(view: View) {
        ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.06f, 1f).apply {
            duration = 1400L
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
        ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.06f, 1f).apply {
            duration = 1400L
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
    }

    fun animateProgress(progressBar: ProgressBar, targetProgress: Int) {
        progressBar.progress = 0
        ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress).apply {
            duration = 700L
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
}
