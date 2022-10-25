package com.ionix.demontir.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.model.api.response.OrderResponse
import com.ionix.demontir.model.api.response.UserInfoResponse
import com.ionix.demontir.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val chatRoomSnapshot = mutableStateOf<DataSnapshot?>(null)
    var channel_id by mutableStateOf("")
    val chatState = mutableStateOf("")

    private val _otherUser = MutableStateFlow<Resource<UserInfoResponse>?>(Resource.Loading())
    val otherUser get() = _otherUser

    private val _myUser = MutableStateFlow<Resource<UserInfoResponse>?>(Resource.Loading())
    val myUser get() = _myUser

    private val _orderDetail = MutableStateFlow<Resource<OrderResponse>?>(Resource.Loading())
    val orderDetail get() = _orderDetail

    fun getOtherUserInfoById(uid1: String, uid2: String) {
        if ((repository.getCurrentUid() ?: "").equals(uid1)) {
            // so other persons uid must be uid 2
            viewModelScope.launch {
                repository.getUserInfoById(uid2).collect {
                    _otherUser.value = it
                }
            }
        } else if ((repository.getCurrentUid() ?: "").equals(uid2)) {
            // so other persons uid must be uid 1
            viewModelScope.launch {
                repository.getUserInfoById(uid1).collect {
                    _otherUser.value = it
                }
            }
        } else {
            /*TODO should be handled soon*/
        }
    }

    fun getAvailableChatRoom(
        possibleChannel1: String,
        possibleChannel2: String,
        user_1: String,
        user_2: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit
    ) = repository.getAvailableChatChannel(
        possibleChannel1,
        possibleChannel2,
        user_1,
        user_2,
        onSuccess,
        onFailed
    )

    fun listenToChatByChannelId(
        channel_id: String,
        onDataChange: (DataSnapshot) -> Unit,
        onCancelled: () -> Unit
    ) = repository.listenChatByChannelId(channel_id, onDataChange, onCancelled)

    fun getOrderDetailByOrderId(order_id:String){
        viewModelScope.launch {
            repository.getOrderDetailByOrderId(order_id).collect{
                _orderDetail.value = it
            }
        }
    }

    fun sendChat(sender:String, receiver:String, onSuccess: () -> Unit, onFailed: () -> Unit) =
        repository.sendChat(
            channel_id = channel_id,
            chat = chatState.value,
            sender = sender,
            receiver = receiver,
            onSuccess = onSuccess,
            onFailed = onFailed
        )

    init {
        viewModelScope.launch {
            repository.getUserInfoById(repository.getCurrentUid() ?: "").collect{
                _myUser.value = it
            }
        }
    }
}