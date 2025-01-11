package com.matchify.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.matchify.R
import com.matchify.databinding.ActivityWelcomeBinding
import com.matchify.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : BaseActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val slides = listOf(
        "Find your match on Matchify",
        "Find your soulmate on Matchify"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLottieAnimation()
        setupViewPager()
        setupDotIndicators()
        setupAutoSlide()
        setupClickListeners()
    }

    private fun setupLottieAnimation() {
        binding.lottieHearts.apply {
            setAnimation(R.raw.hearts_animation)
            playAnimation()
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = WelcomeSlideAdapter(slides)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updateDotIndicators(position)
            }
        })
    }

    private fun setupDotIndicators() {
        val dots = Array(slides.size) { ImageView(this) }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        dots.forEach { dot ->
            dot.setImageResource(R.drawable.dot_indicator_inactive)
            binding.dotsIndicator.addView(dot, params)
        }
        dots[0].setImageResource(R.drawable.dot_indicator_active)
    }

    private fun updateDotIndicators(position: Int) {
        for (i in 0 until binding.dotsIndicator.childCount) {
            val dot = binding.dotsIndicator.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == position) R.drawable.dot_indicator_active
                else R.drawable.dot_indicator_inactive
            )
        }
    }

    private fun setupAutoSlide() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage == slides.lastIndex) {
                    currentPage = 0
                } else {
                    currentPage++
                }
                binding.viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    private fun setupClickListeners() {
        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}