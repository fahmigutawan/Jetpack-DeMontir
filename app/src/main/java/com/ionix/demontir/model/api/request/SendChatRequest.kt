package com.ionix.demontir.model.api.request

data class SendChatRequest(
    val chat_id:String,
    val channel_id:String,
    val sender:String,
    val receiver:String,
    val chat:String
)
