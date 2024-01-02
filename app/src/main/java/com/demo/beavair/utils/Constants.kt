package com.demo.beavair.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tuyasdkdemo.R

object Constants {

    const val TAG = "TUYADEMO"
    const val USER_MODEL = "user_model"
    const val HOME_ID = "home_id"
    const val DEVICE_ID = "device_id"
    const val HOME_DETAIL = "home_detail"
    const val WHITE_COLOR = "white_color"
    const val BG_ALL_COLOR = "bg_all_color"

    const val PTZ_CONTROL  = "119";
    const val PTZ_UP = "0"
    const val PTZ_LEFT = "2"
    const val PTZ_DOWN = "4"
    const val PTZ_RIGHT = "6"

    const val EXTERNAL_STORAGE_REQ_CODE = 10
    const val EXTERNAL_AUDIO_REQ_CODE = 11

    const val ARG1_OPERATE_SUCCESS = 0
    const val ARG1_OPERATE_FAIL = 1

    const val MSG_CONNECT = 2033
    const val MSG_CREATE_DEVICE = 2099
    const val MSG_SET_CLARITY = 2054

    const val MSG_TALK_BACK_FAIL = 2021
    const val MSG_TALK_BACK_BEGIN = 2022
    const val MSG_TALK_BACK_OVER = 2023
    const val MSG_DATA_DATE = 2035

    const val MSG_MUTE = 2024
    const val MSG_SCREENSHOT = 2017

    const val MSG_VIDEO_RECORD_FAIL = 2018
    const val MSG_VIDEO_RECORD_BEGIN = 2019
    const val MSG_VIDEO_RECORD_OVER = 2020

    const val MSG_GET_VIDEO_CLARITY = 2053


    public fun changeStatusBarColor(activity: AppCompatActivity,type: String) {
        val window: Window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if(type == WHITE_COLOR){
            window.statusBarColor = ContextCompat.getColor(activity.applicationContext, R.color.white)
        }else{
            window.statusBarColor = ContextCompat.getColor(activity.applicationContext, R.color.bg_All)
        }
    }

    fun requestPermission(
        context: Context,
        permission: String,
        requestCode: Int,
        tip: String
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        return if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as Activity),
                    permission
                )
            ) {
                ToastUtil.shortToast(context, tip)
            } else {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(permission),
                    requestCode
                )
            }
            false
        } else {
            true
        }
    }

    @SuppressLint("all")
    fun hasRecordPermission(): Boolean {
        val minBufferSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSizeInBytes = 640
        val audioData = ByteArray(bufferSizeInBytes)
        var readSize = 0
        var audioRecord: AudioRecord? = null
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.DEFAULT, 8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize
            )
            // start recording
            audioRecord.startRecording()
        } catch (e: Exception) {
            audioRecord?.release()
            return false
        }
        return if (audioRecord.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord?.stop()
            audioRecord?.release()
            false
        } else {
            readSize = audioRecord.read(audioData, 0, bufferSizeInBytes)
            // Check whether the recording result can be obtained
            audioRecord?.stop()
            audioRecord?.release()
            return readSize>0
        }
    }
}