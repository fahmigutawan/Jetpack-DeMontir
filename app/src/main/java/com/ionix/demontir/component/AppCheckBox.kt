package com.ionix.demontir.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ionix.demontir.ui.theme.BluePrussian

@Composable
fun AppCheckBox(
    boolState: MutableState<Boolean>
) {
    Checkbox(
        checked = boolState.value,
        onCheckedChange = { boolState.value = it },
        colors = CheckboxDefaults.colors(
            checkedColor = BluePrussian,
            uncheckedColor = Color.LightGray,
            checkmarkColor = Color.White
        )
    )
}