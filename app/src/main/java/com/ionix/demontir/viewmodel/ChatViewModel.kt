package com.ionix.demontir.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.model.api.response.GetChatDataFromFirestoreResponse
import com.ionix.demontir.model.api.response.UserInfoResponse
import com.ionix.demontir.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val listOfChat = mutableStateListOf<GetChatDataFromFirestoreResponse>()
    val listOfChatLoadingCount = mutableStateOf(0)

    fun getListOfChat(uid:String) {
        viewModelScope.launch {
            async {
                repository.getListOfChatRoomUid1(uid).collect {
                    when (it) {
                        is Resource.Error -> {
                            listOfChatLoadingCount.value += 1
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            it.data?.let {
                                it.forEach {
                                    listOfChat.add(it)
                                }
                            }

                            listOfChatLoadingCount.value += 1
                        }
                        null -> {}
                    }
                }
            }.join()
            async {
                repository.getListOfChatRoomUid2(uid).collect {
                    when (it) {
                        is Resource.Error -> {
                            listOfChatLoadingCount.value += 1
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            it.data?.let {
                                it.forEach {
                                    listOfChat.add(it)
                                }
                            }

                            listOfChatLoadingCount.value += 1
                        }
                        null -> {}
                    }
                }
            }.join()
        }
    }

    fun getCurrentUid() = repository.getCurrentUid()

    fun getUserInfoByUid(uid: String, onSuccess: (UserInfoResponse) -> Unit, onFailed: () -> Unit) {
        viewModelScope.launch {
            repository.getUserInfoById(uid).collect {
                when (it) {
                    is Resource.Error -> {
                        onFailed()
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        it.data?.let {
                            onSuccess(it)
                        }
                    }
                    null -> {

                    }
                }
            }
        }
    }

    fun getChatCountByChannelId(channel_id:String, onSuccess: (Int) -> Unit, onFailed: () -> Unit) =
        repository.getChatCountByChannelId(channel_id, onSuccess, onFailed)

    init {
        getListOfChat(getCurrentUid() ?: "")
    }
}