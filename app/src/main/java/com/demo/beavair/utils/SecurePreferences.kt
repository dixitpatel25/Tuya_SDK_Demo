package com.demo.beavair.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

object SecurePreferences {
    fun savePreferences(context: Context, key: String?, value: String?) {
        val preferences = context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun savePreferences(context: Context, key: String?, value: Int) {
        val preferences = context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun savePreferences(context: Context, key: String?, value: Long) {
        val preferences = context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun savePreferences(
        context: Context, key: String?,
        value: Boolean?
    ) {
        val preferences = context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(key, value!!)
        editor.apply()
    }

    fun getStringPreference(context: Context?, key: String?): String {
        var value = ""
        if (context != null) {
            val preferences = context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
            value = preferences.getString(key, "").toString()
        }
        return value
    }

    fun getIntegerPreference(context: Context, key: String?): Int {
        val preferences =
            context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        return preferences.getInt(key, 0)
    }

    fun getLongPreference(context: Context, key: String?): Long {
        val preferences =
            context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        return preferences.getLong(key, 0)
    }

    fun getBooleanPreference(
        context: Context,
        key: String?
    ): Boolean {
        val preferences =
            context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        return preferences.getBoolean(key, false)
    }

    fun clearSecurepreference(context: Context) {
        val preferences = context.getSharedPreferences("appdata", Context.MODE_PRIVATE)
        preferences.edit().clear().apply()
    }

}