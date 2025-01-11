package com.matchify.presentation.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import com.matchify.data.model.User
import com.matchify.databinding.ActivityDashboardBinding
import com.matchify.presentation.LoginActivity
import com.matchify.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity() {
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user from intent
        intent.getParcelableExtra<User>(LoginActivity.EXTRA_USER)?.let { user ->
            setupUI(user)
        } ?: run {
            showError("User data not found")
            finish()
        }
    }

    private fun setupUI(user: User) {
        binding.tvWelcome.text = "Hello, ${user.firstName}!"
    }
}