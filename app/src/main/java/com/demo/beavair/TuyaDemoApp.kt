package com.demo.beavair

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.thingclips.smart.home.sdk.ThingHomeSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TuyaDemoApp : Application(){

    override fun onCreate() {
        super.onCreate()
        // For dark mode disable
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Tuya sdk initialization
        ThingHomeSdk.init(this);

        // Tuya sdk Debug mode for check logs if any error present but you release this apk remove this
        ThingHomeSdk.setDebugMode(true);
    }

}