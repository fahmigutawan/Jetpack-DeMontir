package com.ionix.demontir.model.api.response

data class OrderProductsResponse(
    val order_id:String? = null,
    val product_id:String? = null,
    val quantity:Int? = null,
    val sub_total_price:Int? = null
)
