package com.touhid.composeform.session.data

import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.auth.TokenProvider
import com.touhid.composeform.network.safeApiCall
import com.touhid.composeform.session.domain.SessionRepository
import com.touhid.composeform.session.domain.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SessionRepositoryImpl @Inject constructor(
    private val api: SessionApi,
    private val tokenProvider: TokenProvider,
) : SessionRepository {

    // :app's TokenProvider reads EncryptedSharedPreferences, which can touch disk and decrypt.
    override suspend fun isSignedIn(): Boolean = withContext(Dispatchers.IO) {
        !tokenProvider.getToken().isNullOrBlank()
    }

    override suspend fun requestOtp(phoneNumber: String, appHash: String?): AppResult<Unit> =
        safeApiCall { api.requestOtp(OtpRequestDto(phoneNumber = phoneNumber, appHash = appHash)) }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<UserSession> =
        safeApiCall { api.verifyOtp(OtpVerifyRequestDto(phoneNumber = phoneNumber, otp = otp)) }.map { response ->
            tokenProvider.setToken(response.token)
            tokenProvider.setRefreshToken(response.refreshToken)
            UserSession(userId = response.userId, name = response.name, role = response.role)
        }
}
