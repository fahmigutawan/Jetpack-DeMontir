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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarResult.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.*
import com.ionix.demontir.component.AppButtonField
import com.ionix.demontir.ui.theme.DeMontirTheme
import com.ionix.demontir.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

lateinit var mainViewModel: MainViewModel
lateinit var scaffoldState: ScaffoldState
lateinit var snackbarListener: @Composable (text: String, state: MutableState<Boolean>) -> Unit

@AndroidEntryPoint
class MyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            /**Attrs*/
            mainViewModel = hiltViewModel()
            scaffoldState = rememberScaffoldState()
            snackbarListener = { text, state ->
                if (state.value) {
                    LaunchedEffect(key1 = true) {
                        val snackbarHost = scaffoldState.snackbarHostState

                        val result = snackbarHost.showSnackbar(
                            message = text,
                            duration = SnackbarDuration.Short,
                            actionLabel = "Tutup"
                        )

                        when (result) {
                            Dismissed -> state.value = false
                            ActionPerformed -> state.value = false
                        }
                    }
                }
            }

            /**Function*/
            if (mainViewModel.showPrototypeAlertDialog) {
                Dialog(onDismissRequest = { mainViewModel.showPrototypeAlertDialog = false }) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Untuk mengecek fitur chat antara user & bengkel dapat menggunakan email & password berikut")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = ">> Bengkel")
                            Text(text = "Email Bengkel: bengkel@demontir.com")
                            Text(text = "Password: password")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = ">> User")
                            Text(text = "Email User: user@demontir.com")
                            Text(text = "Password: password")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "**Login dengan google juga sudah bisa digunakan**")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Fitur livetracking sudah selesai dibuat, " +
                                    "namun sementara sengaja menggunakan dummy location berada di Malang " +
                                    "agar fitur \"bengkel terdekat\" dapat terdeteksi (karena dummy bengkel dibuat di daerah Malang).")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Fitur chat sudah dapat digunakan (dapat dicoba langsung), antara user dan bengkel")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Fitur riwayat order untuk bengkel belum dapat digunakan, karena belum ada app khusus untuk bengkel")
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                                AppButtonField(onClick = { mainViewModel.showPrototypeAlertDialog = false }) {
                                    Text(text = "Mengerti", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }


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