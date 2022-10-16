package com.ionix.demontir

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.*
import com.ionix.demontir.ui.theme.DeMontirTheme
import com.ionix.demontir.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

lateinit var mainViewModel:MainViewModel
lateinit var scaffoldState:ScaffoldState

@AndroidEntryPoint
class MyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            /**Attrs*/
            mainViewModel = hiltViewModel()
            scaffoldState = rememberScaffoldState()

            /**Function*/


            /**Content*/
            DeMontirTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    AppContent()
                }
            }
        }
    }
}

@HiltAndroidApp
class MyApplication : Application()