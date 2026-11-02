package com.touhid.composeform.feature.login.data

import com.touhid.composeform.feature.login.domain.LoginRepository
import com.touhid.composeform.model.AppError
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.network.safeApiCall
import com.touhid.composeform.session.domain.SessionRepository
import javax.inject.Inject

internal class LoginRepositoryImpl @Inject constructor(
    private val api: LoginApi,
    private val sessionRepository: SessionRepository,
) : LoginRepository {

    override suspend fun requestOtp(phoneNumber: String, appHash: String?): AppResult<Unit> =
        safeApiCall { api.requestOtp(OtpRequestDto(phoneNumber = phoneNumber, appHash = appHash)) }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): AppResult<Unit> {
        val response = when (val result = safeApiCall { api.verifyOtp(OtpVerifyRequestDto(phoneNumber, otp)) }) {
            is AppResult.Failure -> return result
            is AppResult.Success -> result.data
        }
        // A role this app has no home for: don't start a session it couldn't show.
        val user = response.toUserSession()
            ?: return AppResult.Failure(AppError.Unknown("This account type (${response.role}) can't use the app"))
        sessionRepository.startSession(user, accessToken = response.token, refreshToken = response.refreshToken)
        return AppResult.Success(Unit)
    }
}
