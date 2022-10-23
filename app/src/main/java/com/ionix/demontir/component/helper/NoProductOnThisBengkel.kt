package com.ionix.demontir.component.helper

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
fun NoProductOnThisBengkel(
    modifier:Modifier = Modifier.fillMaxWidth()
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(modifier = Modifier.size(32.dp), model = R.drawable.ic_no_product, contentDescription = "Error")
        Text(text = "Tidak ada produk pada bengkel ini", textAlign = TextAlign.Center, fontSize = 20.sp)
    }
}