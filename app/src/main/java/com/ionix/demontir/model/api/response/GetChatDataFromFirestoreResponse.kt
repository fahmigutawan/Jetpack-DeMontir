package com.ionix.demontir.model.api.response

data class GetChatDataFromFirestoreResponse(
    val channel_id:String? = null,
    val user_1:String? = null,
    val user_2:String? = null
)