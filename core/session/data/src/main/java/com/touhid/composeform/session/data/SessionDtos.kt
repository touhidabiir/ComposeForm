package com.touhid.composeform.session.data

// camelCase on the wire already, so no @SerializedName needed.

internal data class OtpRequestDto(
    val phoneNumber: String,
    // The SMS Retriever hash the server appends to the SMS; null is sent as an absent field.
    val appHash: String?,
)

internal data class OtpVerifyRequestDto(
    val phoneNumber: String,
    val otp: String,
)

internal data class LoginResponseDto(
    val token: String,
    val refreshToken: String,
    val userId: String,
    val name: String,
    val role: String,
)
