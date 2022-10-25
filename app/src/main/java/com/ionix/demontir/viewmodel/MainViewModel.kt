package com.ionix.demontir.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.navigation.MainNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val showBottomBar = mutableStateOf(false)
    val bottomBarState = mutableStateOf(MainNavigation.SplashScreen.name)
    val isLoggedIn = mutableStateOf<Boolean?>(null)
    val isFirstTimeEnteringApp = mutableStateOf<Boolean?>(null)
    val searchState = mutableStateOf("")
    val showDashboardItem = mutableStateOf(true)
    var showPrototypeAlertDialog by mutableStateOf(true)

    fun navigateWithDelay(
        delay: Long = 1000,
        navController: NavController,
        route: String,
        inclusive: Boolean = false,
        popBackStackUntil: String? = null
    ) {
        viewModelScope.launch {
            delay(delay)
            if (inclusive) {
                if (popBackStackUntil != null) {
                    navController.popBackStack(route = popBackStackUntil, inclusive)
                } else {
                    navController.popBackStack()
                }
            }
            navController.navigate(route = route)
        }
    }

    fun getFirstTimeEnteringAppState() {
        viewModelScope.launch {
            repository.isEnteringAppFirstTime().collect {
                isFirstTimeEnteringApp.value = it
            }
        }
    }

    fun getIsLoggedInState() {
        isLoggedIn.value = repository.isLoggedIn()
    }

    init {
        getFirstTimeEnteringAppState()
    }
}