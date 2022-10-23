package com.ionix.demontir.model.api.request

data class OrderProductRequest(
    val product_id:String,
    val quantity:Int,
    val sub_total_price:String
)
