package com.touhid.composeform.network.model

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val userId: String,
    val name: String,
    val role: String,
)
