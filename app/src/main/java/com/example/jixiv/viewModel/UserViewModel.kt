package com.example.jixiv.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jixiv.utils.Comment
import com.example.jixiv.utils.User

class UserViewModel : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun login(user: User) {
        _user.value = user
    }

    fun logout() {
        _user.value = null
    }
}