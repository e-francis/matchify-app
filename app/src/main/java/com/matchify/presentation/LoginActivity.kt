package com.matchify.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.matchify.databinding.ActivityLoginBinding
import com.matchify.presentation.auth.login.LoginViewModel
import com.matchify.presentation.base.BaseActivity
import com.matchify.presentation.dashboard.DashboardActivity
import com.matchify.utils.NetworkResult
import com.matchify.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputValidations()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupInputValidations() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateInputs()
            }
        }

        with(binding) {
            etEmail.addTextChangedListener(textWatcher)
            etPasscode.addTextChangedListener(textWatcher)
        }
    }

    private fun validateInputs() {
        with(binding) {
            val isEmailValid = !etEmail.text.isNullOrEmpty()
            val isPasscodeValid = !etPasscode.text.isNullOrEmpty() &&
                    etPasscode.text?.length == 6

            btnLogin.isEnabled = isEmailValid && isPasscodeValid
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            if (validateForm()) {
                viewModel.login(
                    email = binding.etEmail.text.toString(),
                    passcode = binding.etPasscode.text.toString().toInt().toString()
                )
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        with(binding) {
            if (etEmail.text.isNullOrEmpty()) {
                tilEmail.error = "Email is required"
                isValid = false
            } else {
                tilEmail.error = null
            }

            if (etPasscode.text.isNullOrEmpty()) {
                tilPasscode.error = "Passcode is required"
                isValid = false
            } else if (etPasscode.text?.length != 6) {
                tilPasscode.error = "Passcode must be 6 digits"
                isValid = false
            } else {
                tilPasscode.error = null
            }
        }
        return isValid
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    showLoading("Logging in...")
                }
                is NetworkResult.Success -> {
                    hideLoading()
                    showStatusDialog(
                        isSuccess = true,
                        title = "Login Successful",
                        message = "Welcome back!",
                        buttonText = "Continue to Dashboard"
                    ) {
                        startActivity(Intent(this, DashboardActivity::class.java)
                            .putExtra(EXTRA_USER, result.data))
                        finishAffinity()
                    }
                }
                is NetworkResult.Error -> {
                    hideLoading()
                    showStatusDialog(
                        isSuccess = false,
                        title = "Login Failed",
                        message = result.message,
                        buttonText = "Try Again"
                    ) {}
                    showToast(result.message)
                }
            }
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}