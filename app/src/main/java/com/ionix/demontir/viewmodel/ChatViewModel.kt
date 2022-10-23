package com.ionix.demontir.viewmodel

import androidx.lifecycle.ViewModel
import com.ionix.demontir.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {
}