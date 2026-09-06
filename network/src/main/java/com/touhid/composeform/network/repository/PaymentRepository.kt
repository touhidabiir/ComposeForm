package com.touhid.composeform.network.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.PaymentApiService
import com.touhid.composeform.network.model.PaymentChargeRequest
import com.touhid.composeform.network.model.PaymentChargeResponse
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

// The constructor is internal (not the class) because PaymentApiService is internal to
// :network - see AppRepository's constructor for why.
class PaymentRepository @Inject internal constructor(
    private val apiService: PaymentApiService,
) {

    suspend fun charge(amount: Long, currency: String): NetworkResult<PaymentChargeResponse> =
        safeApiCall { apiService.charge(PaymentChargeRequest(amount, currency)) }
}
