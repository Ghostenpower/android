package com.example.jixiv.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    var username: String
        get() = sharedPreferences.getString("key_username", "") ?: ""
        set(value) = sharedPreferences.edit().putString("key_username", value).apply()

    var password: String
        get() = sharedPreferences.getString("key_password", "") ?: ""
        set(value) = sharedPreferences.edit().putString("key_password", value).apply()

    var checked: Boolean
        get() = sharedPreferences.getBoolean("key_checked", false)
        set(value) = sharedPreferences.edit().putBoolean("key_checked", value).apply()
}