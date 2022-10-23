package com.ionix.demontir.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ionix.demontir.R

@Composable
fun AppErrorAlert(modifier: Modifier = Modifier.fillMaxWidth()) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(modifier = Modifier.size(32.dp), model = R.drawable.ic_error_connection, contentDescription = "Error")
        Text(text = "Gagal mengambil data, cek koneksi anda", textAlign = TextAlign.Center, fontSize = 20.sp)
    }
}