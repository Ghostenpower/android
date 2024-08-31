package com.example.jixiv.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class RegisterViewModel:ViewModel() {
    private val _username = mutableStateOf("")
    private val _password = mutableStateOf("")
    private val _repassword = mutableStateOf("")
    val username: State<String> get() = _username
    val password: State<String> get() = _password
    val repassword: State<String> get() = _repassword

    // 更新用户名的方法
    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }
    fun updateRePassword(newRePassword: String) {
        _repassword.value = newRePassword
    }
}