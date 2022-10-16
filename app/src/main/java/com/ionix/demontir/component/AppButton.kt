package com.ionix.demontir.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ionix.demontir.ui.theme.BluePrussian

@Composable
fun AppButtonField(
    modifier: Modifier = Modifier,
    height: Dp = 42.dp,
    onClick: () -> Unit,
    backgroundColor: Color = BluePrussian,
    disabledBackgroudnColor: Color = Gray,
    rippleColor: Color = White,
    enabled:Boolean = true,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Unspecified,
    shape: Shape = RoundedCornerShape(8.dp),
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .background(if(enabled) backgroundColor else disabledBackgroudnColor)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = rippleColor),
                onClick = onClick
            ),
        contentAlignment = contentAlignment
    ) {
        Box(
            modifier = Modifier.height(height).padding(top = 12.dp, bottom = 12.dp, end = 24.dp, start = 24.dp)
        ) {
            content()
        }
    }
}