//package com.example.jixiv.utils
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//
//object Repository {
//    private val _shareList = MutableLiveData<List<Share>>(emptyList())
//    val shareList: LiveData<List<Share>> get() = _shareList
//
//    private val _currentShareIndex = MutableLiveData<Int>(0)
//    val currentShareIndex: LiveData<Int> get() = _currentShareIndex
//
//    fun updateShareList(newList: List<Share>) {
//        _shareList.value = newList
//    }
//
//    fun addShareList(newList:List<Share>){
//        newList.forEach{ addShareToList(it)}
//    }
//
//    fun addShareToList(share: Share) {
//        val updatedList = _shareList.value?.toMutableList() ?: mutableListOf()
//        updatedList.add(share)
//        _shareList.value = updatedList
//    }
//
//    fun updateCurrentShareIndex(newIndex: Int) {
//        _currentShareIndex.value = newIndex
//    }
//}
