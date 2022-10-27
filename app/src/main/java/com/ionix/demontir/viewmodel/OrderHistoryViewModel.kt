package com.ionix.demontir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.model.api.response.OrderResponse
import com.ionix.demontir.model.api.response.UserInfoResponse
import com.ionix.demontir.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {
    private val _orders = MutableStateFlow<Resource<List<OrderResponse>>?>(Resource.Loading())
    val orders get() = _orders

    fun getOrders(){
        viewModelScope.launch {
            repository.getOrderListByUserId(
                user_id = repository.getCurrentUid() ?: ""
            ).collect{
                _orders.value = it
            }
        }
    }

    fun getUserInfoByUid(uid:String, onSuccess:(UserInfoResponse) -> Unit){
        viewModelScope.launch {
            repository.getUserInfoById(uid).collect{
                if(it is Resource.Success){
                    it.data?.let(onSuccess)
                }
            }
        }
    }

    fun getCurentUid() = repository.getCurrentUid() ?: ""

    init {
        getOrders()
    }
}