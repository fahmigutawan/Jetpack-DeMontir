package com.ionix.demontir.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ionix.demontir.component.AppButtonField
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<ProfileViewModel>()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading)

    /**Function*/

    /**Content*/
    SwipeRefresh(state = swipeRefreshState, onRefresh = { /*TODO*/ }) {
        ProfileContent(viewModel = viewModel, navController = navController)
    }
}

@Composable
private fun ProfileContent(viewModel: ProfileViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Karena masih prototipe, maka halaman profil belum tersedia",
            textAlign = TextAlign.Center
        )
        AppButtonField(onClick = {
            viewModel.logout(
                delay = 1500,
                afterDelay = {
                    navController.navigate(route = MainNavigation.LoginScreen.name){
                        popUpTo(route = MainNavigation.LoginScreen.name){
                            inclusive = true
                        }
                    }
                }
            )
        }) {
            Text(text = "Logout", color = Color.White)
        }
    }
}