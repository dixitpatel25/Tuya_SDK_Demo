package com.demo.beavair.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.demo.beavair.utils.Constants
import com.example.tuyasdkdemo.R
import com.thingclips.smart.home.sdk.ThingHomeSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // change status bar color
        Constants.changeStatusBarColor(this,Constants.BG_ALL_COLOR)
        setContentView(R.layout.activity_main)
    }
}