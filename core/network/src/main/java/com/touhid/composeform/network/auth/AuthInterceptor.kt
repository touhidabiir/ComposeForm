package com.touhid.composeform.network.auth

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal const val AUTHORIZATION_HEADER = "Authorization"
internal const val BEARER_PREFIX = "Bearer "

internal class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider.getToken() ?: return chain.proceed(chain.request())
        val authorizedRequest = chain.request().newBuilder()
            .addHeader(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
            .build()
        return chain.proceed(authorizedRequest)
    }
}
