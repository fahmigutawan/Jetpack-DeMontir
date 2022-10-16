package com.ionix.demontir.component

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
import com.ionix.demontir.ui.theme.MistyRose

@Composable
fun AppSnackbar(
    modifier: Modifier = Modifier.padding(32.dp),
    hostState: SnackbarHostState,
    actionOnNewLine: Boolean = false,
    shape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = MistyRose,
    elevation: Dp = 4.dp
) {
    SnackbarHost(hostState = hostState) {
        Snackbar(
            modifier = modifier,
            actionOnNewLine = actionOnNewLine,
            shape = shape,
            backgroundColor = backgroundColor,
            elevation = elevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it.message)
                it.actionLabel?.let { label ->
                    TextButton(onClick = { it.performAction() }) {
                        Text(text = label)
                    }
                }
            }
        }
    }
}