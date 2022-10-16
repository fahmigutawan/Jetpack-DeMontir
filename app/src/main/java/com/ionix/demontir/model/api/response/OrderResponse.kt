package com.ionix.demontir.model.api.response

data class OrderResponse(
    val order_id:String? = null,
    val user_id:String? = null,
    val total_price:Int? = null,
    val user_long:String? = null,
    val user_lat:String? = null
)
