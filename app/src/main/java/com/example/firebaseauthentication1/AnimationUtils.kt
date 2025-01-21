package com.example.firebaseauthentication1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.TranslateAnimation

object AnimationUtils {

    fun animateLoginScreen(
        vararg views: View
    ) {

        resetViewsBeforeAnimation(*views)


        val animators = views.mapIndexed { index, view ->
            createSlideInAnimator(view, (index + 1) * 200L)
        }


        AnimatorSet().apply {
            playTogether(animators)
            start()
        }
    }


    private fun resetViewsBeforeAnimation(vararg views: View) {
        views.forEach { view ->
            view.alpha = 0f
            view.translationY = 100f
            view.scaleX = 0.8f
            view.scaleY = 0.8f
        }
    }


    private fun createSlideInAnimator(view: View, delay: Long): AnimatorSet {
        val translationY = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f).apply {
            duration = 600
            startDelay = delay
            interpolator = AnticipateOvershootInterpolator(1.2f)
        }

        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f).apply {
            duration = 500
            startDelay = delay
            interpolator = OvershootInterpolator(1.2f)
        }

        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f).apply {
            duration = 500
            startDelay = delay
            interpolator = OvershootInterpolator(1.2f)
        }

        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = delay
            interpolator = AccelerateDecelerateInterpolator()
        }

        return AnimatorSet().apply {
            playTogether(translationY, scaleX, scaleY, alphaAnimator)
        }
    }


    fun createButtonPressAnimation(button: View) {
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }


    fun createFragmentTransitionAnimation(view: View) {
        view.alpha = 0f
        view.translationX = 100f
        view.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }


    fun createShakeAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.shake_animation)
    }


    fun createCustomShakeAnimation(): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.1f,
            Animation.RELATIVE_TO_SELF, 0.1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 100
            repeatCount = 3
            repeatMode = Animation.REVERSE
        }
    }


    fun fadeInAnimation(view: View) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }


    fun fadeOutAnimation(view: View, onEnd: () -> Unit = {}) {
        view.animate()
            .alpha(0f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction(onEnd)
            .start()
    }


    fun scaleAnimation(view: View, scale: Float = 0.9f) {
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(200)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
    }
}