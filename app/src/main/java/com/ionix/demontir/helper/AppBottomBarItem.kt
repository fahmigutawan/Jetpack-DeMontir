package com.ionix.demontir.helper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.ionix.demontir.navigation.MainNavigation

enum class AppBottomBarItem(val word: String, val icon: ImageVector, val route: String) {
    Home("Home", Icons.Default.Home, MainNavigation.HomeScreen.name),
    Chat("Chat", Icons.Default.Chat, MainNavigation.ChatScreen.name),
    History("History", Icons.Default.History, MainNavigation.HistoryScreen.name),
    Profile("Profile", Icons.Default.AccountCircle, MainNavigation.ProfileScreen.name)
}