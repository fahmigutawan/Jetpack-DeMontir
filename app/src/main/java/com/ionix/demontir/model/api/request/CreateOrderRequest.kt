package com.ionix.demontir.model.api.request

data class CreateOrderRequest(
    val order_id:String? = null,
    val order_status:Int? = null,
    val user_id:String? = null,
    val bengkel_id:String? = null,
    val total_price:String? = null,
    val user_long:String? = null,
    val user_lat:String? = null
)