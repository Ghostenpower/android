package com.example.jixiv.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.jixiv.R


//
open class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
    }

    private fun setStatusBarColor() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false // Optional: For dark background
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
    }
}