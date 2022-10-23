package com.ionix.demontir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.demontir.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {
    var isLoading by mutableStateOf(false)
    fun logout(delay:Long, afterDelay:() -> Unit){
        viewModelScope.launch {
            repository.logout()
            delay(delay)
            afterDelay()
        }
    }
}