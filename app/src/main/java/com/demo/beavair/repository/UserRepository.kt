package com.demo.beavair.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.beavair.models.UserRequest
import com.demo.beavair.models.UserResponse
import com.demo.beavair.utils.NetworkResult
import com.thingclips.smart.android.user.api.ILoginCallback
import com.thingclips.smart.android.user.api.IRegisterCallback
import com.thingclips.smart.android.user.bean.User
import com.thingclips.smart.home.sdk.ThingHomeSdk
import com.thingclips.smart.sdk.api.IResultCallback
import javax.inject.Inject

class UserRepository @Inject constructor(){

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = _userResponseLiveData

    fun registerUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        ThingHomeSdk.getUserInstance().registerAccountWithEmail(
            userRequest.countryCode,
            userRequest.email,
            userRequest.password,
            userRequest.verificationCode,
            object : IRegisterCallback{
                override fun onSuccess(user: User?) {
                    val userResponse = UserResponse("Register successfully",user)
                    _userResponseLiveData.postValue(NetworkResult.Success(userResponse))
                }

                override fun onError(code: String?, error: String?) {
                    _userResponseLiveData.postValue(NetworkResult.Error("$code --> $error"))
                }
            }
        )
    }

    fun loginUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        ThingHomeSdk.getUserInstance()
            .loginWithEmail(userRequest.countryCode, userRequest.email, userRequest.password, object : ILoginCallback{
                override fun onSuccess(user: User?) {
                    val userResponse = UserResponse("Login successfully",user)
                    _userResponseLiveData.postValue(NetworkResult.Success(userResponse))
                }

                override fun onError(code: String?, error: String?) {
                    _userResponseLiveData.postValue(NetworkResult.Error("$code --> $error"))
                }
            })
    }

    fun sendVerificationCode(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(
            userRequest.email,
            "",
            userRequest.countryCode,
            1,
            object : IResultCallback {
                override fun onSuccess() {
                    val userResponse = UserResponse("Code sent successfully",null)
                    _userResponseLiveData.postValue(NetworkResult.Success(userResponse))
                }

                override fun onError(code: String?, error: String?) {
                    _userResponseLiveData.postValue(NetworkResult.Error("$code --> $error"))
                }

            })
    }

}