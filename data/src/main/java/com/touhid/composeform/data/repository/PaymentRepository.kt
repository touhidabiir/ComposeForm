package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.PaymentApiService
import com.touhid.composeform.network.model.PaymentChargeRequest
import com.touhid.composeform.network.model.PaymentChargeResponse
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val apiService: PaymentApiService,
) {

    suspend fun charge(amount: Long, currency: String): NetworkResult<PaymentChargeResponse> =
        safeApiCall { apiService.charge(PaymentChargeRequest(amount, currency)) }
}
