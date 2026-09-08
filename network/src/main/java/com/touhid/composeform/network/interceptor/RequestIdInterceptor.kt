package com.touhid.composeform.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID
import javax.inject.Inject

// Unlike HeaderInterceptor's fixed header map, a request ID must be regenerated for every
// request rather than fixed once when the client is built - so it needs its own Interceptor,
// with the UUID generated inside intercept() itself. Stateless and not per-client configuration,
// so RetrofitFactory adds it to every base URL rather than it being passed through the
// interceptors list.
internal class RequestIdInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Request-Id", UUID.randomUUID().toString())
            .build()
        return chain.proceed(request)
    }
}
