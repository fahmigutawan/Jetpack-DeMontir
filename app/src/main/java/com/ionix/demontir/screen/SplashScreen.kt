package com.ionix.demontir.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ionix.demontir.R
import com.ionix.demontir.mainViewModel
import com.ionix.demontir.navigation.MainNavigation

@Composable
fun AppSplashScreen(
    backgroundColor: Color = Color.White,
    iconSize: Dp = (LocalConfiguration.current.screenHeightDp / 5).dp,
    navController: NavController
) {
    /**Attrs*/

    /**Function*/
    LaunchedEffect(key1 = true) { mainViewModel.getIsLoggedInState() }

    mainViewModel.isLoggedIn.value?.let { loggedIn ->
        LaunchedEffect(key1 = true) {
            if (loggedIn) {
                if (mainViewModel.isFirstTimeEnteringApp.value != null) {
                    when (mainViewModel.isFirstTimeEnteringApp.value!!) {
                        true -> {
                            mainViewModel.navigateWithDelay(
                                navController = navController,
                                route = MainNavigation.OnboardScreen.name,
                                inclusive = true
                            )
                        }
                        false -> {
                            mainViewModel.navigateWithDelay(
                                navController = navController,
                                route = MainNavigation.HomeScreen.name,
                                inclusive = true
                            )
                        }
                    }
                }
            } else mainViewModel.navigateWithDelay(
                navController = navController,
                route = MainNavigation.LoginScreen.name,
                inclusive = true
            )
        }
    }

    /**Content*/
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.size(iconSize),
            model = R.drawable.ic_logo,
            contentDescription = "Logo"
        )
    }
}