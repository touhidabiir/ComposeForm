package com.touhid.composeform.network.api

import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.ManagerSummary
import retrofit2.http.GET
import retrofit2.http.Path

interface AppApiService {

    @GET("v1/manager/list")
    suspend fun getManagerList(): List<ManagerSummary>

    @GET("v1/admin/list")
    suspend fun getAdminList(): List<AdminSummary>

    @GET("v1/admin/details/{id}")
    suspend fun getAdminDetails(@Path("id") id: String): AdminDetails
}
