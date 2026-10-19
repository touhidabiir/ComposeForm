package com.touhid.composeform.network.interceptor

import com.touhid.composeform.network.qualifier.AppVersionCode
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal const val APP_VERSION_HEADER = "X-Version"

// Tells the backend which platform and build is calling, e.g. "X-Version: android/42". The value
// is fixed for the process, unlike RequestIdInterceptor's per-request ID, but every client wants
// it, so RetrofitFactory adds it to every client too. header() rather than addHeader() so a
// request that already carries it (e.g. TokenAuthenticator's retry) never ends up with two.
internal class AppVersionInterceptor @Inject constructor(
    @AppVersionCode versionCode: Int,
) : Interceptor {

    private val headerValue = "android/$versionCode"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(APP_VERSION_HEADER, headerValue)
            .build()
        return chain.proceed(request)
    }
}
