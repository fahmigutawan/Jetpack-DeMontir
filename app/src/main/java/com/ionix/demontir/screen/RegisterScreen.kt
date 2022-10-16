package com.ionix.demontir.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ionix.demontir.R
import com.ionix.demontir.component.AppButtonField
import com.ionix.demontir.component.AppTextInputField
import com.ionix.demontir.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<RegisterViewModel>()
    val iconSize = LocalConfiguration.current.screenHeightDp / 6

    /**Function*/

    /**Content*/
    RegisterScreenContent(
        viewModel = viewModel,
        iconSize = iconSize.dp
    )
}

@Composable
private fun RegisterScreenContent(
    viewModel: RegisterViewModel,
    iconSize: Dp
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(64.dp))
        }

        /*Logo*/
        item {
            AsyncImage(
                modifier = androidx.compose.ui.Modifier.height(iconSize),
                model = R.drawable.ic_logo,
                contentDescription = "Logo"
            )
        }

        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        /*Field*/
        item {
            RegisterScreenField(viewModel = viewModel)
        }

        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RegisterScreenField(viewModel: RegisterViewModel) {
    /*Email*/
    Spacer(modifier = Modifier.height(16.dp))
    AppTextInputField(
        modifier = Modifier.fillMaxWidth(),
        placeHolderText = "Email",
        valueState = viewModel.emailState
    )

    /*Password*/
    Spacer(modifier = Modifier.height(16.dp))
    AppTextInputField(
        modifier = Modifier.fillMaxWidth(),
        placeHolderText = "Password",
        valueState = viewModel.passwordState,
        endContent = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        viewModel.showPassword.value = !viewModel.showPassword.value
                    },
                imageVector =
                when (viewModel.showPassword.value) {
                    true -> Icons.Default.VisibilityOff
                    false -> Icons.Default.Visibility
                }, contentDescription = "Visibility"
            )
        },
        visualTransformation = when (viewModel.showPassword.value) {
            true -> VisualTransformation.None
            false -> PasswordVisualTransformation()
        }
    )

    /*Konfirmasi Password*/
    Spacer(modifier = Modifier.height(16.dp))
    AppTextInputField(
        modifier = Modifier.fillMaxWidth(),
        placeHolderText = "Konfirmasi Password",
        valueState = viewModel.confirmPasswordState,
        endContent = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        viewModel.showConfirmPassword.value = !viewModel.showConfirmPassword.value
                    },
                imageVector =
                when (viewModel.showConfirmPassword.value) {
                    true -> Icons.Default.VisibilityOff
                    false -> Icons.Default.Visibility
                }, contentDescription = "Visibility"
            )
        },
        visualTransformation = when (viewModel.showConfirmPassword.value) {
            true -> VisualTransformation.None
            false -> PasswordVisualTransformation()
        }
    )

    /*Kode Autentikase*/
    Spacer(modifier = Modifier.height(16.dp))
    AppTextInputField(
        modifier = Modifier.fillMaxWidth(),
        placeHolderText = "Kode Verifikasi",
        valueState = viewModel.verificationCodeState,
        endContent = {
            Text(
                modifier = Modifier.clickable { /*TODO*/ },
                text = "KIRIM KODE KE EMAIL",
                fontSize = 12.sp,
                color = Color.Blue
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )

    /*Error Message*/
    AnimatedVisibility(visible = viewModel.errorMessage.value.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color.Red
                )
                Text(text = viewModel.errorMessage.value, color = Color.Red, fontSize = 12.sp)
            }
        }
    }

    /*Daftar Button*/
    Spacer(modifier = Modifier.height(16.dp))
    AppButtonField(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            registerFieldChecker(
                viewModel = viewModel,
                onNothingError = {
                    viewModel.registerWithEmailPassword(
                        onSuccess = { /*TODO*/ },
                        onFailed = { /*TODO*/ }
                    )
                }
            )
        }
    ) {
        Text(text = "Daftar", color = Color.White, fontSize = 12.sp)
    }
}

private fun registerFieldChecker(onNothingError: () -> Unit, viewModel: RegisterViewModel) {
    if (viewModel.emailState.value == ""
        && viewModel.passwordState.value == ""
        && viewModel.confirmPasswordState.value == ""
    ) {
        viewModel.errorMessage.value = "Pastikan semua data telah terisi"
        return
    }

    if (!viewModel.passwordState.value.equals(viewModel.confirmPasswordState.value)) {
        viewModel.errorMessage.value = "Password dan Konfirmasi Password tidak sama"
        return
    }

    viewModel.errorMessage.value = ""
    onNothingError()
}