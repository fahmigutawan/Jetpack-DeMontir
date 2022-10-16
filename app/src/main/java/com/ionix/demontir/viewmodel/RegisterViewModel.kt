package com.ionix.demontir.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ionix.demontir.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {
    val emailState = mutableStateOf("")
    val passwordState = mutableStateOf("")
    val showPassword = mutableStateOf(false)
    val confirmPasswordState = mutableStateOf("")
    val showConfirmPassword = mutableStateOf(false)
    val verificationCodeState = mutableStateOf("")
    val errorMessage = mutableStateOf("")

    fun registerWithEmailPassword(
        onFailed: () -> Unit,
        onSuccess: () -> Unit
    ) =
        repository.registerWithEmailPassword(emailState.value, passwordState.value)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailed() }
}