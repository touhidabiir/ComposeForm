package com.touhid.composeform.network.model

data class RefreshTokenRequest(
    val refreshToken: String,
)

// The backend rotates the refresh token on every refresh - both values are replaced together.
data class RefreshTokenResponse(
    val token: String,
    val refreshToken: String,
)
