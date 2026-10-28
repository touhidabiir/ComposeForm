package com.touhid.composeform.session.data

// camelCase on the wire already, so no @SerializedName needed.

internal data class LoginRequestDto(
    val username: String,
    val password: String,
)

internal data class LoginResponseDto(
    val token: String,
    val refreshToken: String,
    val userId: String,
    val name: String,
    val role: String,
)
