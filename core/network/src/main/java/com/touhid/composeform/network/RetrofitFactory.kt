package com.touhid.composeform.network

import com.touhid.composeform.network.interceptor.RequestIdInterceptor
import com.touhid.composeform.network.mock.MockDataInterceptor
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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
// mapper, ...) without every other client paying for it. authenticator likewise defaults to none -
// only a base URL whose backend actually issues refresh tokens passes one.
internal class RetrofitFactory @Inject constructor(
    private val requestIdInterceptor: RequestIdInterceptor,
    private val loggingInterceptor: HttpLoggingInterceptor,
) {

    fun create(
        baseUrl: String,
        authInterceptor: Interceptor?,
        interceptors: List<Interceptor> = emptyList(),
        authenticator: Authenticator? = null,
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(requestIdInterceptor)
            .apply { authInterceptor?.let(::addInterceptor) }
            .apply { interceptors.forEach(::addInterceptor) }
            .addInterceptor(loggingInterceptor)
            // TODO: remove once the real backend is live - also delete the whole network/mock/
            // package (MockDataInterceptor.kt, MockJson.kt) at the same time, it exists solely to
            // back this call.
            // Debug-gated so a release build can never end up silently serving fake data instead
            // of failing to reach a real backend. Harmless for paths it doesn't mock - it falls
            // through to the real request.
            // Must stay the LAST application interceptor: for a mocked path it returns a canned
            // response without calling chain.proceed(), so any interceptor added after it never
            // runs. Placed here it stands in for only the network transport - the request-id/auth
            // headers are still added, the error mapping still runs, and loggingInterceptor
            // still logs both the request and the mocked response to logcat.
            .apply { if (BuildConfig.DEBUG) addInterceptor(MockDataInterceptor()) }
            .apply { authenticator?.let { this.authenticator(it) } }
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
