package com.demo.beavair.utils

import android.content.Context
import android.view.WindowManager

class DensityUtil {

    companion object{
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        fun dip2pxF(context: Context, dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }

        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        fun px2sp(context: Context, pxValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        fun getScreenDispalyWidth(context: Context): Int {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val width = wm.defaultDisplay.width
            val height = wm.defaultDisplay.height
            return if (width > height) height else width
        }
    }
}