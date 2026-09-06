package com.touhid.composeform.network.auth

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

internal class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenProvider.getToken()
        val skipAuth = request.tag(Invocation::class.java)?.method()?.isAnnotationPresent(NoAuth::class.java) == true
        if (token == null || skipAuth) return chain.proceed(request)
        val authorizedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authorizedRequest)
    }
}
