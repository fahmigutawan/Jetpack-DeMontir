package com.ionix.demontir

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ionix.demontir.component.AppBottomBar
import com.ionix.demontir.component.AppSnackbar
import com.ionix.demontir.navigation.BranchNavigation
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.screen.*

@Composable
fun AppContent() {
    /**Attrs*/
    val navController = rememberNavController()

    /**Function*/
    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        mainViewModel.bottomBarState.value = destination.route ?: ""
    }

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
            AppSplashScreen(navController = navController)
        }

        composable(route = MainNavigation.LoginScreen.name) {
            LoginScreen(navController = navController)
        }

        composable(route = MainNavigation.RegisterScreen.name) {
            RegisterScreen(navController = navController)
        }

        composable(route = MainNavigation.OnboardScreen.name) {
            OnboardScreen(navController = navController)
        }

        composable(route = MainNavigation.HomeScreen.name) {
            HomeScreen(navController = navController, mainViewModel = mainViewModel)
        }

        composable(route = MainNavigation.ChatScreen.name) {
        }

        composable(route = "${MainNavigation.ChatScreen.name}/{uid_1}/{uid_2}") {
        }

        composable(route = "${MainNavigation.ChatScreen.name}/{uid_1}/{uid_2}/{order_id}") {
        }

        composable(route = MainNavigation.HistoryScreen.name) {
        }

        composable(route = MainNavigation.ProfileScreen.name) {
            ProfileScreen(navController = navController)
        }

        /*Branch Screen*/
        composable(
            route = "${BranchNavigation.OrderScreen.name}/{bengkel_id}",
            arguments = listOf(
                navArgument("bengkel_id") { type = NavType.StringType }
            )
        ) {
            val bengkel_id = it.arguments?.getString("bengkel_id")

            BengkelProductScreen(
                navController = navController,
                bengkel_id = bengkel_id ?: ""
            )
        }
    }
}