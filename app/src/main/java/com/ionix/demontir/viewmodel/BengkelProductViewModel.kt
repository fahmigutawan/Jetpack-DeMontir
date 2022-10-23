package com.ionix.demontir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.model.api.request.CreateOrderProductRequest
import com.ionix.demontir.model.api.request.OrderProductRequest
import com.ionix.demontir.model.api.response.BengkelProductResponse
import com.ionix.demontir.model.api.response.BengkelResponse
import com.ionix.demontir.model.non_api.CustomOrder
import com.ionix.demontir.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BengkelProductViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val customOrderList = mutableStateListOf<CustomOrder>()
    var showCustomOrderDialog by mutableStateOf(false)
    val tmpCustomOrderProductName = mutableStateOf("")
    val tmpCustomOrderProductPrice = mutableStateOf("")
    val pickedProduct = mutableStateListOf<BengkelProductResponse>()
    var showBackAlertDialog by mutableStateOf(false)
    var showTitleListOfProduct by mutableStateOf(false)
    val showFailedOrderSnackbar = mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var startOrderNowFlow by mutableStateOf(false)
    val showShouldPickAtLeastOneSnackbar = mutableStateOf(false)
    val showCouldntOrderToSameUid = mutableStateOf(false)
    val showCouldntChatToSameUid = mutableStateOf(false)

    private val _bengkelProducts =
        MutableStateFlow<Resource<List<BengkelProductResponse>>>(Resource.Loading())
    val bengkelProducts get() = _bengkelProducts

    suspend fun getBengkelProductsByBengkelId(bengkel_id: String) {
        repository.getBengkelProductsByBengkelId(bengkel_id = bengkel_id).collect {
            it?.let {
                _bengkelProducts.value = it
            }
        }
    }

    fun createNewOrder(
        total_price: String,
        user_long: String,
        user_lat: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit,
        listOfProduct: List<OrderProductRequest>

    ) = repository.createNewOrder(
        total_price,
        user_long,
        user_lat,
        onSuccess,
        onFailed,
        listOfProduct
    )

    fun getCurrentUid() = repository.getCurrentUid()

    fun shouldShowBackAlertDialog(): Boolean {
        if (pickedProduct.isNotEmpty()) return true
        if (customOrderList.isNotEmpty()) return true
        return false
    }
}