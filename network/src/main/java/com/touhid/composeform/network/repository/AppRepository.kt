package com.touhid.composeform.network.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.AppApiService
import com.touhid.composeform.network.auth.TokenProvider
import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.LoginRequest
import com.touhid.composeform.network.model.LoginResponse
import com.touhid.composeform.network.model.ManagerSummary
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val apiService: AppApiService,
    private val tokenProvider: TokenProvider,
) {

    suspend fun login(username: String, password: String): NetworkResult<LoginResponse> =
        safeApiCall { apiService.login(LoginRequest(username, password)) }.also { result ->
            if (result is NetworkResult.Success) {
                tokenProvider.setToken(result.data.token)
            }
        }

    suspend fun getManagerList(): NetworkResult<List<ManagerSummary>> =
        safeApiCall { apiService.getManagerList() }

    suspend fun getAdminList(): NetworkResult<List<AdminSummary>> =
        safeApiCall { apiService.getAdminList() }

    suspend fun getAdminDetails(id: String): NetworkResult<AdminDetails> =
        safeApiCall { apiService.getAdminDetails(id) }
}
