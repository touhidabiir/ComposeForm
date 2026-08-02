package com.touhid.composeform.network.api

import com.touhid.composeform.network.model.AdminDetails
import com.touhid.composeform.network.model.AdminSummary
import com.touhid.composeform.network.model.LoginRequest
import com.touhid.composeform.network.model.LoginResponse
import com.touhid.composeform.network.model.ManagerSummary
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface AppApiService {

    @POST("v1/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("v1/manager/list")
    suspend fun getManagerList(): List<ManagerSummary>

    @GET("v1/admin/list")
    suspend fun getAdminList(): List<AdminSummary>

    @GET("v1/admin/details/{id}")
    suspend fun getAdminDetails(@Path("id") id: String): AdminDetails
}
