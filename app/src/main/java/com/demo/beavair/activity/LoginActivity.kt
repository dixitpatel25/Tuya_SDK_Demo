package com.demo.beavair.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.demo.beavair.utils.Constants
import com.example.tuyasdkdemo.R
import com.thingclips.smart.home.sdk.ThingHomeSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // checking if user already login then redirect to user home screen
        if (ThingHomeSdk.getUserInstance().isLogin) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
        // for change status bar color
        Constants.changeStatusBarColor(this,Constants.WHITE_COLOR)
        setContentView(R.layout.activity_login)
    }

}