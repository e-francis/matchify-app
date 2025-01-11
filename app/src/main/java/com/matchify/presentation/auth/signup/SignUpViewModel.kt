package com.matchify.presentation.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matchify.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.matchify.data.model.User
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _signUpState = MutableLiveData<SignUpState>()
    val signUpState: LiveData<SignUpState> = _signUpState

    private val userData = MutableStateFlow(
        User(
            firstName = "",
            lastName = "",
            dob = "",
            location = "",
            interests = emptyList(),
            sex = "",
            email = "",
            passcode = 0,
            profilePicture = ""
        )
    )

    fun createProfile() {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val result = userRepository.createProfile(userData.value)
                result.fold(
                    onSuccess = { response ->
                        if (response.success) {
                            _signUpState.value = SignUpState.Success(
                                message = response.message,
                                profileId = response.profileId
                            )
                        } else {
                            _signUpState.value = SignUpState.Error(
                                message = response.message,
                                code = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        when (exception) {
                            is HttpException -> {
                                val errorBody = exception.response()?.errorBody()?.string()
                                _signUpState.value = SignUpState.Error(
                                    message = exception.message ?: "Profile creation failed",
                                    code = exception.code(),
                                    errorBody = errorBody
                                )
                            }
                            else -> {
                                _signUpState.value = SignUpState.Error(
                                    message = exception.message ?: "An unexpected error occurred"
                                )
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(
                    message = e.localizedMessage ?: "An unexpected error occurred"
                )
            }
        }
    }

    // Add update methods for user data
    fun updateUserData(
        firstName: String,
        lastName: String,
        email: String,
        gender: String,
        dob: String,
        location: String,
        passcode: Int,
        interests: List<String>
    ) {
        userData.value = userData.value.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            sex = gender,
            dob = dob,
            location = location,
            passcode = passcode,
            interests = interests
        )
    }

    fun updateProfilePicture(base64Picture: String) {
        userData.value = userData.value.copy(
            profilePicture = base64Picture
        )
    }
}