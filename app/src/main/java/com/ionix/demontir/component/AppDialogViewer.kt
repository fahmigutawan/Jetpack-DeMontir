package com.ionix.demontir.component

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppImageViewerDialog(
    onDissmissRequest: () -> Unit, url: String
) {
    var scale by remember { mutableStateOf(1f) }
    var panX by remember { mutableStateOf(0f) }
    var panY by remember { mutableStateOf(0f) }
    val localDensity = LocalDensity.current
    val scrWidth = LocalConfiguration.current.screenWidthDp
    val scrHeight = LocalConfiguration.current.screenHeightDp
    val scrWidthPx = with(localDensity) { scrWidth.dp.toPx() }
    val scrHeightPx = with(localDensity) { scrHeight.dp.toPx() }

    Dialog(
        onDismissRequest = onDissmissRequest, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false
        )
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale, scaleY = scale, translationX = panX, translationY = panY
                )
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        scale *= zoom

                        if(panX<0){
                            if (panX > (-scrWidthPx/1.2 + 35)) panX += pan.x
                            else panX += 20
                        }else{
                            if (panX < (scrWidthPx/1.2 - 35)) panX += pan.x
                            else panX -= 20
                        }

                        Log.e("PANY", panY.toString())
                        Log.e("SCR HEIGHT", scrHeightPx.toString())

                        if(panY < 0){
                            if(panY > (-scrHeightPx/2 + 35)) panY += pan.y
                            else panY += 20
                        }else{
                            if(panY < (scrHeightPx/2 - 35)) panY += pan.y
                            else panY -= 20
                        }
                    }
                }, model = url, contentDescription = "Image"
        )
    }
}






















