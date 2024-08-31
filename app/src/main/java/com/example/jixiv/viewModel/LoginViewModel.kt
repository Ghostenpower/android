package com.example.jixiv.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.jixiv.activity.MainActivity
import com.example.jixiv.retrofit.ExampleRepository
import com.example.jixiv.utils.User
import com.example.jixiv.utils.clearPreferenceKey
import com.example.jixiv.utils.saveToPreferences
import kotlinx.coroutines.launch

class LoginViewModel(initUsername:String="",initPassword:String="",initChecked:Boolean=false) : ViewModel() {
    private val e = ExampleRepository()
    private var isLoggingIn = false // 节流标志

    private val _username = MutableLiveData("")
    val username: LiveData<String> get() = _username
    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    private val _password = MutableLiveData("")
    val password: LiveData<String> get() = _password
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    private val _checked = MutableLiveData(false)
    val checked: LiveData<Boolean> get() = _checked
    fun updateChecked(newChecked: Boolean) {
        _checked.value = newChecked
    }

    init {
        _username.value=initUsername
        _password.value=initPassword
        _checked.value=initChecked
    }


    fun login(context: Context) {
        viewModelScope.launch {
            if (isLoggingIn) return@launch // 节流

            isLoggingIn = true
            val usernameValue = _username.value.orEmpty()
            val passwordValue = _password.value.orEmpty()

            if (usernameValue.isEmpty() || passwordValue.isEmpty()) {
                Toast.makeText(context, "用户名或密码不完整", Toast.LENGTH_SHORT).show()
                isLoggingIn = false
                return@launch
            }
            // 调用 repository 的 login 方法
            val result = e.login(usernameValue, passwordValue)
            when {
                result.isSuccess -> {
                    val user: User = result.getOrNull()!!.data // 假设返回的结构中包含 User
                    saveToPreferences(context, "user", user)
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? Activity)?.finish()
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                    if(_checked.value!!){
                        saveToPreferences<String>(context,"username",_username.value!!)
                        saveToPreferences<String>(context,"password",_password.value!!)
                    }else{
                        clearPreferenceKey(context,"username")
                        clearPreferenceKey(context,"password")
                    }
                    saveToPreferences(context,"checked",_checked.value)
                }
                else -> {
                    Toast.makeText(context, result.exceptionOrNull()?.message ?: "登录失败", Toast.LENGTH_SHORT).show()
                }
            }
            isLoggingIn = false // 重置节流标志
        }
    }

}
