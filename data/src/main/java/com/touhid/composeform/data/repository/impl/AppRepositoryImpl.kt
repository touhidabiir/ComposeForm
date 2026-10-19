package com.touhid.composeform.data.repository.impl

import com.touhid.composeform.data.repository.AppRepository
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.api.AppApiService
import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.ManagerSummary
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiService: AppApiService,
) : AppRepository {

    override suspend fun getManagerList(): NetworkResult<List<ManagerSummary>> =
        safeApiCall { apiService.getManagerList() }

    override suspend fun getAdminList(): NetworkResult<List<AdminSummary>> =
        safeApiCall { apiService.getAdminList() }

    override suspend fun getAdminDetails(id: String): NetworkResult<AdminDetails> =
        safeApiCall { apiService.getAdminDetails(id) }
}
