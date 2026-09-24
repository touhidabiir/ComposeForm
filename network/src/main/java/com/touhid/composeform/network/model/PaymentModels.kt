package com.touhid.composeform.network.model

data class PaymentChargeRequest(
    val amount: Long,
    val currency: String,
)

data class PaymentChargeResponse(
    val transactionId: String,
    val status: String,
)
