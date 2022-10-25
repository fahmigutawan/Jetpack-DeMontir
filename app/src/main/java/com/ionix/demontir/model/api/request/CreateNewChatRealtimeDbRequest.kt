package com.ionix.demontir.model.api.request

data class CreateNewChatRealtimeDbRequest(
    val channel_id:String,
    val count:Int,
    val user_1:String,
    val user_2:String
)
