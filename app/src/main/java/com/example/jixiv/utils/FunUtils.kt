package com.example.jixiv.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@SuppressLint("SimpleDateFormat")
fun timestampToDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = Date(timestamp)
    return sdf.format(date)
}
// 泛型函数用于通过 SharedPreferences 储存对象
inline fun <reified T> saveToPreferences(context: Context, key: String, obj: T) {
    val gson = Gson()
    val json = gson.toJson(obj)
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(key, json)
    editor.apply()
}

// 泛型函数用于从 SharedPreferences 获取对象
inline fun <reified T> getFromPreferences(context: Context, key: String): T? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(key, null)
    return json?.let {
        val gson = Gson()
        gson.fromJson(json, T::class.java)
    }
}
//泛型函数用于清除 SharedPreferences 指定键值对
fun clearPreferenceKey(context: Context, key: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove(key)
    editor.apply()
}

fun getCurrentTime(): String {
    return Instant.now()
        .atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT)
}
