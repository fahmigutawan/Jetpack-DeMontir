package com.ionix.demontir.screen

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.GoogleAuthProvider
import com.ionix.demontir.R
import com.ionix.demontir.component.AppButtonField
import com.ionix.demontir.component.AppTextInputField
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.snackbarListener
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.ui.theme.BlueQueen
import com.ionix.demontir.viewmodel.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Composable
fun LoginScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<LoginViewModel>()
    val context = LocalContext.current
    val iconSize = LocalConfiguration.current.screenHeightDp / 6

    /**Function*/
    snackbarListener("Login gagal, coba lagi nanti", viewModel.showErrorSnackbar)
    val launcher = googleLoginLauncher(
        context = context,
        viewModel = viewModel,
        onSuccess = {
            viewModel.isFirstTimeEnteringApp.value?.let { firstTime ->
                /*TODO Save user info to database*/
                viewModel.saveUserInfoToFirestore(
                    onSuccess = {
                        if (firstTime) {
                            // Navigate to Onboard
                            navController.navigate(route = MainNavigation.OnboardScreen.name){
                                popUpTo(route = MainNavigation.LoginScreen.name){
                                    inclusive = true
                                }
                            }
                        } else {
                            // Navigate to Home
                            navController.navigate(route = MainNavigation.HomeScreen.name){
                                popUpTo(route = MainNavigation.LoginScreen.name){
                                    inclusive = true
                                }
                            }
                        }
                    },
                    onFailed = {
                        viewModel.showErrorSnackbar.value = true
                    }
                )
            }
        },
        onFailed = { viewModel.showErrorSnackbar.value = true }
    )

    /**Content*/
    LoginScreenContent(
        onRegularLoginClick = {
            regularLoginErrorChecker(
                viewModel = viewModel,
                onNothingError = {
                    viewModel.loginWithEmailPassword(
                        onSuccess = {
                            viewModel.isFirstTimeEnteringApp.value?.let { firstTime ->
                                viewModel.saveUserInfoToFirestore(
                                    onSuccess = {
                                        if (firstTime) {
                                            // Navigate to Onboard
                                            navController.navigate(route = MainNavigation.OnboardScreen.name)
                                        } else {
                                            // Navigate to Home
                                            navController.navigate(route = MainNavigation.HomeScreen.name)
                                        }
                                    },
                                    onFailed = {
//                                      viewModel.showErrorSnackbar.value = true
                                    }
                                )
                            }
                        },
                        onFailed = {}
                    )
                }
            )
        },
        onGoogleLoginClick = { viewModel.loginWithGoogle(launcher) },
        onRegisterClick = { navController.navigate(route = MainNavigation.RegisterScreen.name) },
        iconSize = iconSize.dp,
        viewModel = viewModel
    )
}

@Composable
private fun LoginScreenContent(
    onRegularLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    iconSize: Dp,
    viewModel: LoginViewModel
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
                modifier = Modifier.height(iconSize),
                model = R.drawable.ic_logo,
                contentDescription = "Logo"
            )
        }

        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        /*Title*/
        item {
            Text(
                text = "Bereskan masalah kendaraan anda bersama kami",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        /*Field*/
        item {
            LoginContentField(
                onRegularLoginClick = onRegularLoginClick,
                onGoogleLoginClick = onGoogleLoginClick,
                onRegisterClick = onRegisterClick,
                viewModel = viewModel
            )
        }

        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LoginContentField(
    onRegularLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel
) {
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

    /*Login Button*/
    Spacer(modifier = Modifier.height(16.dp))
    AppButtonField(
        modifier = Modifier.fillMaxWidth(),
        onClick = onRegularLoginClick
    ) {
        Text(text = "Masuk", color = Color.White, fontSize = 12.sp)
    }

    /*Daftar*/
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Belum punya akun?", fontSize = 12.sp, color = Color.Black)
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable(onClick = onRegisterClick),
            text = "Daftar",
            fontSize = 12.sp,
            color = BlueQueen
        )
    }

    /*Divider*/
    Spacer(modifier = Modifier.height(16.dp))
    Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = BluePrussian)

    /*Login Google*/
    Spacer(modifier = Modifier.height(16.dp))
    AppButtonField(
        modifier = Modifier.fillMaxWidth(),
        onClick = onGoogleLoginClick,
        backgroundColor = Color.White,
        rippleColor = Color.Black,
        borderWidth = 1.dp,
        borderColor = BluePrussian
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google",
                tint = Color.Unspecified
            )
            Text(text = "Masuk dengan Google", color = BlueQueen, fontSize = 12.sp)
        }
    }
}


@Composable
private fun googleLoginLauncher(
    onSuccess: () -> Unit,
    onFailed: () -> Unit,
    context: Context,
    viewModel: LoginViewModel
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
) { result ->
    val intent = result.data
    val oneTapClient = Identity.getSignInClient(context)

    when (result.resultCode) {
        Activity.RESULT_OK -> {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)

            if (credential.googleIdToken != null) {
                // Token retrieved
                val fbCredential = GoogleAuthProvider
                    .getCredential(credential.googleIdToken, null)

                viewModel.loginWithCredential(fbCredential)
                    .addOnSuccessListener {
                        onSuccess()
                    }.addOnFailureListener {
                        onFailed()
                    }
            } else {
                // Token hasn't retrieved
                Log.e("GOOGLE SIGN IN", "TOKEN WASN'T RETRIEVED")
                onFailed()
            }
        }
        else -> {
            if (result.data?.action == ActivityResultContracts.StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST) {
                // Couldn't start One Tap UI
                Log.e("GOOGLE SIGN IN", "COULDN'T START ONE TAP UI")
                onFailed()
            }
        }
    }
}

private fun regularLoginErrorChecker(onNothingError: () -> Unit, viewModel: LoginViewModel) {
    if (viewModel.emailState.value == ""
        || viewModel.passwordState.value == ""
    ) {
        viewModel.errorMessage.value = "Pastikan semua data telah terisi"
        return
    }

    if (!viewModel.emailState.value.contains("@")) {
        viewModel.errorMessage.value = "Masukkan email yang benar"
        return
    }

    viewModel.errorMessage.value = ""
    onNothingError()
}
