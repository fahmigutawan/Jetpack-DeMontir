package com.ionix.demontir.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ionix.demontir.helper.AppBottomBarItem
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.ui.theme.BlueCyanLight
import com.ionix.demontir.ui.theme.BluePowder
import com.ionix.demontir.ui.theme.BluePrussian

@Composable
fun AppBottomBar(
    navController: NavController,
    showState: MutableState<Boolean>,
    navigationState: MutableState<String>
) {
    /**Attrs*/

    /**Function*/
    when (navigationState.value) {
        MainNavigation.HomeScreen.name -> showState.value = true

        MainNavigation.ChatScreen.name -> showState.value = true

        MainNavigation.HistoryScreen.name -> showState.value = true

        MainNavigation.ProfileScreen.name -> showState.value = true

        else -> showState.value = false
    }

    /**Content*/
    if (showState.value) {
        BottomAppBar(backgroundColor = BlueCyanLight) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBottomBarItem.values().forEach {
                    IconButton(onClick = { navController.navigate(route = it.route) }) {
                        when (navigationState.value) {
                            it.route -> Icon(
                                imageVector = it.icon,
                                contentDescription = it.name,
                                tint = BluePrussian
                            )

                            else -> Icon(
                                imageVector = it.icon,
                                contentDescription = it.name,
                                tint = BluePowder
                            )
                        }
                    }
                }
            }
        }
    }
}