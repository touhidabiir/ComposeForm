package com.touhid.composeform.data.repository

import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.ManagerSummary

// The contract consumers should inject - impl.AppRepositoryImpl is the only implementation today,
// but declaring this as an interface (rather than injecting AppRepositoryImpl directly) lets a
// ViewModel test substitute a fake implementation instead of a real AppApiService.
interface AppRepository {

    suspend fun getManagerList(): NetworkResult<List<ManagerSummary>>

    suspend fun getAdminList(): NetworkResult<List<AdminSummary>>

    suspend fun getAdminDetails(id: String): NetworkResult<AdminDetails>
}
