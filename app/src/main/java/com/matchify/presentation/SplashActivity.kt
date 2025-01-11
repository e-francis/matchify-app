package com.matchify.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import com.matchify.databinding.ActivitySplashBinding
import com.matchify.presentation.base.BaseActivity
import com.matchify.presentation.model.Ad
import com.matchify.presentation.splash.adapter.AdsPagerAdapter
import com.matchify.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        startAutoSlide()
    }

    private fun setupViewPager() {
        val ads = listOf(
            Ad("Find your match on Matchify", R.drawable.ad_image_1),
            Ad("Find your soulmate on Matchify", R.drawable.ad_image_2)
        )

        binding.viewPager.apply {
            adapter = AdsPagerAdapter(ads)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentPage = position
                }
            })
        }
    }

    private fun startAutoSlide() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage == 1) {
                    navigateToWelcome()
                } else {
                    binding.viewPager.currentItem = ++currentPage
                    handler.postDelayed(this, 3000)
                }
            }
        }, 3000)
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}