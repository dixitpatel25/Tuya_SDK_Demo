package com.demo.beavair.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.beavair.models.HomeResponse
import com.demo.beavair.models.UserRequest
import com.demo.beavair.models.UserResponse
import com.demo.beavair.utils.Constants
import com.demo.beavair.utils.NetworkResult
import com.demo.beavair.utils.SecurePreferences
import com.thingclips.bouncycastle.asn1.x500.style.RFC4519Style.name
import com.thingclips.smart.android.user.api.ILoginCallback
import com.thingclips.smart.android.user.api.IRegisterCallback
import com.thingclips.smart.android.user.bean.User
import com.thingclips.smart.home.sdk.ThingHomeSdk
import com.thingclips.smart.home.sdk.bean.HomeBean
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback
import com.thingclips.smart.sdk.api.IResultCallback
import kotlinx.coroutines.delay
import javax.inject.Inject

class HomeRepository @Inject constructor() {

    private val _homeResponseLiveData = MutableLiveData<NetworkResult<HomeResponse>>()
    val homeResponseLiveData: LiveData<NetworkResult<HomeResponse>> get() = _homeResponseLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    fun fetchCurrentHomeDetails(homeID: Long) {
        ThingHomeSdk.newHomeInstance(homeID)
            .getHomeDetail(object : IThingHomeResultCallback {
                override fun onSuccess(bean: HomeBean?) {
                    bean?.let {
                        val homeResponse = HomeResponse(Constants.HOME_DETAIL, it)
                        _homeResponseLiveData.postValue(NetworkResult.Success(homeResponse))
                    }
                }

                override fun onError(errorCode: String?, errorMsg: String?) {
                    Log.d(Constants.TAG, "$errorCode --> $errorMsg")
                }
            })
    }

    fun fetchHomeList(context:Context) {
        // Query home list from server
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(object : IThingGetHomeListCallback {
            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
                if (homeBeans != null && homeBeans.size > 0) {
                    SecurePreferences.savePreferences(context, Constants.HOME_ID, homeBeans.get(0).homeId)
                    fetchCurrentHomeDetails(homeBeans.get(0).homeId)
                } else {
                    val homeResponse = HomeResponse(Constants.HOME_DETAIL, null)
                    _homeResponseLiveData.postValue(NetworkResult.Success(homeResponse))
                }
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                Log.d(Constants.TAG, "$errorCode --> $errorMsg")
            }

        })
    }

    fun createNewHome(context: Context, name: String) {
        ThingHomeSdk.getHomeManagerInstance().createHome(
            name,
            // Get location by yourself, here just sample as Shanghai's location
            120.52,
            30.40,
            "Rajkot",
            arrayListOf(),
            object : IThingHomeResultCallback {
                override fun onSuccess(bean: HomeBean?) {
                    SecurePreferences.savePreferences(context, Constants.HOME_ID, bean?.homeId ?: 0)
                    _statusLiveData.postValue(
                        NetworkResult.Success(
                            Pair(
                                true,
                                "Home create successfully"
                            )
                        )
                    )
                }

                override fun onError(errorCode: String?, errorMsg: String?) {
                    _statusLiveData.postValue(
                        NetworkResult.Success(
                            Pair(
                                false,
                                "$errorCode --> $errorMsg"
                            )
                        )
                    )
                }
            }
        )
    }
}