package com.ionix.demontir.model.api.request

data class CreateNewChatDataFirestoreRequest(
    val channel_id:String,
    val user_1:String,
    val user_2:String
)
