package com.ionix.demontir.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTextInputField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    height: Dp = 42.dp,
    placeHolderText: String,
    placeHolderColor: Color = Gray,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(fontSize = 12.sp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    leadingContent: @Composable (() -> Unit)? = null,
    endContent: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    valueState: MutableState<String>,
    backgroundColor: Color = White
) {
    Box(
        modifier = modifier
            .height(height)
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.CenterEnd,
    ) {
        BasicTextField(
            value = valueState.value,
            onValueChange = { valueState.value = it },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation
        ) { field ->
            Box(contentAlignment = Alignment.CenterStart) {
                field()
                if (valueState.value.isEmpty()) Text(
                    text = placeHolderText,
                    style = textStyle,
                    color = placeHolderColor
                )
            }
        }
        Box(
            modifier = modifier
                .fillMaxHeight()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                leadingContent?.let { leading ->
                    leading()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                endContent?.let { end ->
                    Spacer(modifier = Modifier.width(8.dp))
                    end()
                }
            }
        }
    }
}

@Composable
fun AppClickableInputField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit,
    height: Dp = 42.dp,
    placeHolderText: String,
    placeHolderColor: Color = Gray,
    enabled: Boolean = true,
    readOnly: Boolean = true,
    textStyle: TextStyle = TextStyle(fontSize = 12.sp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    leadingContent: @Composable (() -> Unit)? = null,
    endContent: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    valueState: MutableState<String>,
    backgroundColor: Color = White
) {
    Box(
        modifier = modifier
            .height(height)
            .border(width = 1.dp, color = Gray, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = modifier
                .height(height)
                .border(width = 1.dp, color = Gray, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.CenterEnd
        ) {
            BasicTextField(
                value = valueState.value,
                onValueChange = { valueState.value = it },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                visualTransformation = visualTransformation
            ) { field ->
                Box(contentAlignment = Alignment.CenterStart) {
                    field()
                    if (valueState.value.isEmpty()) Text(
                        text = placeHolderText,
                        style = textStyle,
                        color = placeHolderColor
                    )
                }
            }


            Box(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    leadingContent?.let { leading ->
                        leading()
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    endContent?.let { end ->
                        Spacer(modifier = Modifier.width(8.dp))
                        end()
                    }
                }
            }
        }

        Box(
            modifier = modifier
                .height(height)
                .fillMaxWidth()
                .border(width = 1.dp, color = Gray, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = Gray),
                    onClick = onClick
                )
        )
    }
}