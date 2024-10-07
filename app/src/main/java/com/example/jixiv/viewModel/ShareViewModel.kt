package com.example.jixiv.viewModel

import android.util.Log
import androidx.compose.material.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jixiv.utils.Share
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ShareViewModel() : ViewModel() {
    private val _selectedTab = MutableLiveData(0)
    val selectedTab: LiveData<Int> get() = _selectedTab

    fun updateSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    private val _refresh=MutableLiveData(0)
    val refresh:LiveData<Int> = _refresh

    fun updateRefresh(){
        _refresh.value=_refresh.value!!+1
    }

    private val _selectedTab1 = MutableLiveData(0)
    val selectedTab1: LiveData<Int> get() = _selectedTab1

    fun updateSelectedTab1(tabIndex: Int) {
        Log.e("updateSelectedTab1", "updateSelectedTab1:$tabIndex ", )
        _selectedTab1.value = tabIndex
    }

    private val _shares = MutableLiveData<List<Share>>(emptyList())
    val shares: LiveData<List<Share>> = _shares

    fun updateShares(shares:List<Share>){
        val filteredShares = shares.filter { it.imageUrlList.isNotEmpty() }
        _shares.value=filteredShares
        Log.e("updateShares", "addShares: ${_shares.value}", )
    }

    fun addShares(items: List<Share>) {
        // Retrieve the current list
        val currentShares = _shares.value.orEmpty()

        // Filter new items to ensure they have image URLs
        val filteredItems = items.filter { it.imageUrlList.isNotEmpty() }

        // Combine current shares with new filtered items
        val updatedShares = currentShares + filteredItems

        // Update LiveData with the new list
        _shares.value = updatedShares
        Log.e("addShares", "addShares: ${_shares.value}", )
    }

    private val _currents = MutableLiveData(1)
    val currents: LiveData<Int> = _currents

    fun updateCurrents(newCurrents:Int){
        _currents.value=newCurrents
    }

    private val _drawerIsClose=MutableLiveData(true)
    val drawerIsClose:LiveData<Boolean> = _drawerIsClose

    fun updateDrawerIsClose(state:Boolean){
        _drawerIsClose.value=state
    }

    private val _drawerState = MutableStateFlow(DrawerValue.Closed)
    val drawerState: StateFlow<DrawerValue> = _drawerState

    fun toggleDrawer() {
        _drawerState.update {
            if (it == DrawerValue.Closed) DrawerValue.Open else DrawerValue.Closed
        }
    }
}