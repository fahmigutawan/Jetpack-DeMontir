package com.ionix.demontir

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ionix.demontir.component.AppBottomBar
import com.ionix.demontir.component.AppSnackbar
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.screen.*

@Composable
fun AppContent() {
    /**Attrs*/
    val navController = rememberNavController()

    /**Content*/
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = { AppSnackbar(hostState = it) },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                showState = mainViewModel.showBottomBar,
                navigationState = mainViewModel.bottomBarState
            )
        }
    ) {
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        )
    }
}

@Composable
private fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainNavigation.SplashScreen.name
    ) {
        /*Main Screen*/
        composable(route = MainNavigation.SplashScreen.name) {
            mainViewModel.bottomBarState.value = MainNavigation.SplashScreen.name
            AppSplashScreen(navController = navController)
        }

        composable(route = MainNavigation.LoginScreen.name){
            mainViewModel.bottomBarState.value = MainNavigation.LoginScreen.name
            LoginScreen(navController = navController)
        }

        composable(route = MainNavigation.RegisterScreen.name){
            mainViewModel.bottomBarState.value = MainNavigation.RegisterScreen.name
            RegisterScreen(navController = navController)
        }

        composable(route = MainNavigation.OnboardScreen.name){
            mainViewModel.bottomBarState.value = MainNavigation.OnboardScreen.name
            OnboardScreen(navController = navController)
        }

        composable(route = MainNavigation.HomeScreen.name) {
            mainViewModel.bottomBarState.value = MainNavigation.HomeScreen.name
            HomeScreen(navController = navController, mainViewModel = mainViewModel)
        }

        composable(route = MainNavigation.ChatScreen.name) {
            mainViewModel.bottomBarState.value = MainNavigation.ChatScreen.name
        }

        composable(route = MainNavigation.HistoryScreen.name) {
            mainViewModel.bottomBarState.value = MainNavigation.HistoryScreen.name
        }

        composable(route = MainNavigation.ProfileScreen.name) {
            mainViewModel.bottomBarState.value = MainNavigation.ProfileScreen.name
        }

        /*Branch Screen*/
    }
}