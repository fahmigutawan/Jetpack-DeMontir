package com.ionix.demontir.model.api.response

data class UserInfoResponse(
    val email: String? = null,
    val name: String? = null,
    val profile_picture: String? = null,
    val uid: String? = null,
    val user_lat: String? = null,
    val user_long: String? = null
)
