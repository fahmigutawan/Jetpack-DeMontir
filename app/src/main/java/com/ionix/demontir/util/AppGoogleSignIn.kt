package com.ionix.demontir.util

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ionix.demontir.R
import javax.inject.Inject

class AppGoogleSignIn @Inject constructor(private val context: Context) {
    fun login(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        val oneTapClient = Identity.getSignInClient(context)
        val googleSignInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.google_signin_client))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(googleSignInRequest)
            .addOnSuccessListener {
                val intentSenderRequest = IntentSenderRequest.Builder(it.pendingIntent).build()
                launcher.launch(intentSenderRequest)
            }
    }
}