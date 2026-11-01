package com.touhid.composeform.feature.login.data

import com.touhid.composeform.feature.login.domain.LoginRepository
import com.touhid.composeform.feature.login.domain.UserSession
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.auth.TokenProvider
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

internal class LoginRepositoryImpl @Inject constructor(
    private val api: LoginApi,
    private val tokenProvider: TokenProvider,
) : LoginRepository {

    override suspend fun requestOtp(phoneNumber: String, appHash: String?): AppResult<Unit> =
        safeApiCall { api.requestOtp(OtpRequestDto(phoneNumber = phoneNumber, appHash = appHash)) }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<UserSession> =
        safeApiCall { api.verifyOtp(OtpVerifyRequestDto(phoneNumber = phoneNumber, otp = otp)) }.map { response ->
            tokenProvider.setToken(response.token)
            tokenProvider.setRefreshToken(response.refreshToken)
            UserSession(userId = response.userId, name = response.name, role = response.role)
        }
}
