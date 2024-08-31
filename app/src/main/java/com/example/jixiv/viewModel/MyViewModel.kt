package com.example.jixiv.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jixiv.retrofit.ExampleRepository
import com.example.jixiv.utils.Share
import com.example.jixiv.utils.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class MyViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            return MyViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MyViewModel:ViewModel() {
    val e=ExampleRepository()

    private val _selectList = MutableLiveData<List<Boolean>>(emptyList())
    val selectList: LiveData<List<Boolean>> = _selectList

    fun resetSelectList() {
        val size = _shares.value?.size ?: 0
        _selectList.value = List(size) { false }
    }

    fun updateSelectList(idx: Int) {
        val currentList = _selectList.value ?: return
        if (idx in currentList.indices) {
            val newList = currentList.toMutableList()
            newList[idx] = !newList[idx]
            _selectList.value = newList
        }
    }

    private val _shares = MutableLiveData<List<Share>>(emptyList())
    val shares: LiveData<List<Share>> = _shares

    fun updateShares(shares:List<Share>){
        _shares.value=shares.filter { it.imageUrlList.isNotEmpty()}
        Log.e("updateShares", "updateShares: ${_shares.value}", )
        resetSelectList()
    }

    private val _isEdit = MutableLiveData(false)
    val isEdit: LiveData<Boolean> get() = _isEdit

    fun updateSelectedTab(isEdit: Boolean) {
        _isEdit.value = isEdit
    }

    fun deleteSeleted() {
        viewModelScope.launch {
            updateIsLoading() // 开始加载
            try {
                _selectList.value?.forEachIndexed { idx, isSelected ->
                    if (isSelected) {
                        val share = _shares.value?.get(idx)
                        share?.let {
                            e.deleteShare(share.id,share.pUserId){}
                        }
                    }
                }
            } catch (e: Exception) {
                // 处理异常，比如记录日志或展示错误信息
                Log.e("ViewModel", "Error during release", e)
            } finally {
                delay(Duration.ofMillis(100))
                updateIsLoading() // 完成加载
                updateSelectedTab(false)
            }
        }
    }

    private val _pageTab=MutableLiveData(0)
    val pageTab:LiveData<Int> =_pageTab

    fun updatePageTab(idx: Int) {
        _pageTab.value=idx
    }

    private var _selectAllTab=true
    fun selectAll() {
        val size = _shares.value?.size ?: 0
        _selectList.value = List(size) { _selectAllTab }
        _selectAllTab=!_selectAllTab
    }

    private val _isLoading=MutableLiveData(false)
    val isLoading:LiveData<Boolean> =_isLoading

    fun updateIsLoading(){
        _isLoading.value=!_isLoading.value!!
    }

    fun release() {
        viewModelScope.launch {
            updateIsLoading() // 开始加载
            try {
                _selectList.value?.forEachIndexed { idx, isSelected ->
                    if (isSelected) {
                        val share = _shares.value?.get(idx)
                        share?.let {
                            e.changeShare(share.content,share.id,share.imageCode,share.pUserId,share.title){}
                        }
                    }
                }
            } catch (e: Exception) {
                // 处理异常，比如记录日志或展示错误信息
                Log.e("ViewModel", "Error during release", e)
            } finally {
                delay(Duration.ofMillis(100))
                updateIsLoading() // 完成加载
                updateSelectedTab(false)
            }
        }
    }
}