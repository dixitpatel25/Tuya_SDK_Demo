package com.demo.beavair.fragment.home

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.beavair.models.HomeResponse
import com.demo.beavair.models.UserRequest
import com.demo.beavair.models.UserResponse
import com.demo.beavair.repository.HomeRepository
import com.demo.beavair.repository.UserRepository
import com.demo.beavair.utils.Constants
import com.demo.beavair.utils.Helper
import com.demo.beavair.utils.NetworkResult
import com.demo.beavair.utils.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {


    val homeResponseLiveData get() = homeRepository.homeResponseLiveData
    val statusLiveData get() = homeRepository.statusLiveData

    fun fetchCurrentHomeDetails(context: Context) {
        val currentHomeID = SecurePreferences.getLongPreference(context, Constants.HOME_ID)
        if (currentHomeID != 0L) {
            viewModelScope.launch {
                homeRepository.fetchCurrentHomeDetails(currentHomeID)
            }
        } else {
            viewModelScope.launch {
                delay(1000)
                homeRepository.fetchHomeList(context)
            }
        }

    }

    fun createNewHome(context: Context, homeName: String) {
        viewModelScope.launch {
            homeRepository.createNewHome(context, homeName)
        }
    }
}