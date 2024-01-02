package com.demo.beavair.fragment.login

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.beavair.models.UserRequest
import com.demo.beavair.models.UserResponse
import com.demo.beavair.repository.UserRepository
import com.demo.beavair.utils.Helper
import com.demo.beavair.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = userRepository.userResponseLiveData

    fun registerUser(userRequest: UserRequest) {
        viewModelScope.launch {
            userRepository.registerUser(userRequest)
        }
    }

    fun loginUser(userRequest: UserRequest) {
        viewModelScope.launch {
            userRepository.loginUser(userRequest)
        }
    }

    fun sendVerificationCode(userRequest: UserRequest) {
        viewModelScope.launch {
            userRepository.sendVerificationCode(userRequest)
        }
    }

    fun validateEmail(emailAddress: String): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (TextUtils.isEmpty(emailAddress)) {
            result = Pair(false, "Please provide email address")
        } else if (!Helper.isValidEmail(emailAddress)) {
            result = Pair(false, "Email is invalid")
        }
        return result
    }

    fun validateCredentialsForRegister(
        emailAddress: String, password: String,
        verifyCode: String
    ): Pair<Boolean, String> {

        var result = Pair(true, "")

        if (TextUtils.isEmpty(emailAddress)) {
            result = Pair(false, "Please provide email address")
        } else if (!Helper.isValidEmail(emailAddress)) {
            result = Pair(false, "Email is invalid")
        } else if (TextUtils.isEmpty(password)) {
            result = Pair(false, "Please provide password")
        } else if (TextUtils.isEmpty(verifyCode)) {
            result = Pair(false, "Please enter verification code")
        }

        return result
    }

    fun validateCredentialsForLogin(
        emailAddress: String, password: String
    ): Pair<Boolean, String> {

        var result = Pair(true, "")

        if (TextUtils.isEmpty(emailAddress)) {
            result = Pair(false, "Please provide email address")
        } else if (!Helper.isValidEmail(emailAddress)) {
            result = Pair(false, "Email is invalid")
        } else if (TextUtils.isEmpty(password)) {
            result = Pair(false, "Please provide password")
        }

        return result
    }
}