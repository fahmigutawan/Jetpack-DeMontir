package com.ionix.demontir.model.api.request

data class UserInfoRequest(
    val uid:String,
    val name:String,
    val email:String,
    val profile_picture:String,
    val user_long:String,
    val user_lat:String
)
