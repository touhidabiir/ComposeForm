package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.PaymentChargeResponse

interface PaymentRepository {
    suspend fun charge(amount: Long, currency: String): NetworkResult<PaymentChargeResponse>
}
