package com.demo.beavair.utils

import android.content.Context
import android.widget.Toast

class ToastUtil {
    companion object{
        fun shortToast(context: Context?, tips: String?) {
            Toast.makeText(context, tips, Toast.LENGTH_SHORT).show()
        }
    }
}