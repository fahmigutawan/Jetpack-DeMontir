package com.ionix.demontir.model.api.response

data class ChatResponse(
    val chat_id:String? = null,
    val channel_id:String? = null,
    val sender:String? = null,
    val receiver:String? = null,
    val chat:String? = null
)