package com.demo.beavair.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.demo.beavair.utils.Constants
import com.demo.beavair.utils.NetworkResult
import com.demo.beavair.utils.SecurePreferences
import com.thingclips.smart.home.sdk.ThingHomeSdk
import com.thingclips.smart.home.sdk.builder.ThingCameraActivatorBuilder
import com.thingclips.smart.sdk.api.IThingActivatorGetToken
import com.thingclips.smart.sdk.api.IThingSmartCameraActivatorListener
import com.thingclips.smart.sdk.bean.DeviceBean
import javax.inject.Inject

class DeviceRepository @Inject constructor() {

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    fun fetchToken(context:Context) {
        val homeID = SecurePreferences.getLongPreference(context,Constants.HOME_ID)
        ThingHomeSdk.getActivatorInstance().getActivatorToken(homeID,
            object : IThingActivatorGetToken {
                override fun onSuccess(token: String) {
                    _statusLiveData.postValue(NetworkResult.Success(Pair(true, token)))
                }

                override fun onFailure(errorCode: String, errorMsg: String) {
                    _statusLiveData.postValue(NetworkResult.Success(Pair(true, "Get Token: $errorCode --> $errorMsg")))
                }
            })
    }
}