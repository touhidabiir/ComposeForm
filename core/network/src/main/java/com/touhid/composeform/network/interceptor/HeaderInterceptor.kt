package com.touhid.composeform.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

// Reusable static-header injection for a single base URL (e.g. a partner/payment backend's own
// API key) - construct with whatever headers that base URL needs and pass it into
// RetrofitFactory.create()'s interceptors list.
internal class HeaderInterceptor(private val headers: Map<String, String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .apply { headers.forEach { (name, value) -> addHeader(name, value) } }
            .build()
        return chain.proceed(request)
    }
}
