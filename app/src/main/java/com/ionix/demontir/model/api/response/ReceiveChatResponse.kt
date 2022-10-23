package com.ionix.demontir.model.api.response

data class ReceiveChatResponse(
    val chat_id:String,
    val channel_id:String,
    val sender:String,
    val receiver:String,
    val chat:String
)
