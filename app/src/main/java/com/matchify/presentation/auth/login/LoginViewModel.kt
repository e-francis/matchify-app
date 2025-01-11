package com.matchify.presentation.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matchify.data.model.User
import com.matchify.data.repository.UserRepository
import com.matchify.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<NetworkResult<User>>()
    val loginState: LiveData<NetworkResult<User>> = _loginState

    fun login(email: String, passcode: String) {
        viewModelScope.launch {
            _loginState.value = NetworkResult.Loading
            try {
                val result = userRepository.login(email, passcode)
                result.fold(
                    onSuccess = { loginResponse ->
                        if (loginResponse.success && loginResponse.user != null) {
                            _loginState.value = NetworkResult.Success(loginResponse.user)
                        } else {
                            _loginState.value = NetworkResult.Error(
                                message = loginResponse.message,
                                code = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _loginState.value = NetworkResult.Error(
                            message = exception.message ?: "Login failed",
                            code = null
                        )
                    }
                )
            } catch (e: Exception) {
                _loginState.value = NetworkResult.Error(
                    message = e.message ?: "An unexpected error occurred",
                    code = null
                )
            }
        }
    }
}