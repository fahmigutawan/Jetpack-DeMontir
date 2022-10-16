package com.ionix.demontir.model.api.response

data class BengkelProductResponse(
    val product_id:String? = null,
    val product_name:String? = null,
    val product_price:String? = null,
    val product_stock:String? = null,
    val bengkel_id:String? = null,
    val category_id:String? = null
)
