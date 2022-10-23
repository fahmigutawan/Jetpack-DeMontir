package com.ionix.demontir.model.api.request

data class CreateOrderProductRequest(
    val order_id:String,
    val product_id:String,
    val quantity:Int,
    val sub_total_price:String
)
