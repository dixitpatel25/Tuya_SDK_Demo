package com.demo.beavair.fragment.qrcode

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.beavair.models.WifiRequest
import com.demo.beavair.repository.DeviceRepository
import com.demo.beavair.utils.Helper
import com.thingclips.smart.sdk.api.IThingCameraDevActivator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceConfingViewModel @Inject constructor(private val deviceRepository: DeviceRepository) : ViewModel() {


    val statusLiveData get() = deviceRepository.statusLiveData

    fun fetchToken(context: Context) {
        viewModelScope.launch {
            deviceRepository.fetchToken(context)
        }
    }

    fun checkValidation(
        wifiName: String, wifiPassword: String
    ): Pair<Boolean, String> {

        var result = Pair(true, "")

        if (TextUtils.isEmpty(wifiName)) {
            result = Pair(false, "Please provide wifi name")
        }  else if (TextUtils.isEmpty(wifiPassword)) {
            result = Pair(false, "Please provide wifi password")
        }

        return result
    }
}