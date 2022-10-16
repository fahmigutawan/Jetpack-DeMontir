package com.ionix.demontir.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.screen.OnboardContentPage1
import com.ionix.demontir.screen.OnboardContentPage2
import com.ionix.demontir.screen.OnboardContentPage3
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(private val repository: AppRepository):ViewModel() {
    /**Attrs*/
    val pageState = mutableStateOf(0)

    /**Function*/
    fun saveIsFirstTimeEnteringApp() {
        viewModelScope.launch {
            repository.saveIsEnteringAppFirstTime()
        }
    }
}