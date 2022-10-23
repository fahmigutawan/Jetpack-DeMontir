package com.ionix.demontir.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ionix.demontir.ui.theme.BluePowder
import com.ionix.demontir.ui.theme.MistyRose

@Composable
fun AppSnackbar(
    modifier: Modifier = Modifier.padding(16.dp),
    hostState: SnackbarHostState,
    actionOnNewLine: Boolean = false,
    shape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = BluePowder,
    elevation: Dp = 4.dp
) {
    SnackbarHost(hostState = hostState) {
        Snackbar(
            modifier = modifier,
            actionOnNewLine = actionOnNewLine,
            shape = shape,
            backgroundColor = backgroundColor,
            elevation = elevation,
            contentColor = Color.Black,
            action = {
                Text(
                    text = "Tutup",
                    modifier = Modifier
                        .clickable { it.performAction() }
                        .padding(end = 8.dp),
                    color = Color.Red
                )
            }
        ) {
            Text(text = it.message)
        }
    }
}