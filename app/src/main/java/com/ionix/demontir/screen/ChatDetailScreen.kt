package com.ionix.demontir.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ionix.demontir.viewmodel.ChatDetailViewModel

@Composable
fun ChatDetailScreen(
    uid_1:String,
    uid_2:String,
    order_id:String = "",
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<ChatDetailViewModel>()

    /**Function*/

    /**Content*/
}