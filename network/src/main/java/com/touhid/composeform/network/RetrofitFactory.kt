package com.touhid.composeform.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TIMEOUT_SECONDS = 30L

// One factory shared by every base URL in NetworkModule - each create() call composes its own
// OkHttpClient/Retrofit from exactly the interceptors that base URL needs, rather than forcing
// every client through one fixed shared chain. authInterceptor is nullable so a base URL that
// must never carry this app's bearer token (e.g. a third-party partner backend) simply omits it;
// interceptors covers anything else that base URL needs (a static header, a bespoke error
// mapper, ...) without every other client paying for it.
internal class RetrofitFactory @Inject constructor(
    private val loggingInterceptor: HttpLoggingInterceptor,
) {

    fun create(
        baseUrl: String,
        authInterceptor: Interceptor?,
        interceptors: List<Interceptor> = emptyList(),
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            // TODO: remove once the real backend is live - also delete MockDataInterceptor.kt and
            // MockJson.kt (network/) at the same time, they exist solely to back this call.
            // Debug-gated so a release build can never end up silently serving fake data instead
            // of failing to reach a real backend. Harmless for base URLs that don't match any of
            // its paths (AppApiService's) - it falls through to the real request.
            .apply { if (BuildConfig.DEBUG) addInterceptor(MockDataInterceptor()) }
            .apply { authInterceptor?.let(::addInterceptor) }
            .apply { interceptors.forEach(::addInterceptor) }
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}
