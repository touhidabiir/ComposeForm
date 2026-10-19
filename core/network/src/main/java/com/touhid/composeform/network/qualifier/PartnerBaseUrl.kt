package com.touhid.composeform.network.qualifier

import com.touhid.composeform.network.NetworkModule
import com.touhid.composeform.network.RetrofitFactory
import com.touhid.composeform.network.auth.AuthInterceptor
import javax.inject.Qualifier

/**
 * Binds the partner service's Retrofit base URL - same deferral pattern as [BaseUrl]: a
 * consuming module's own Hilt module provides the value (e.g.
 * `@Provides @PartnerBaseUrl fun providePartnerBaseUrl(): String = "https://..."`). Requires a
 * trailing slash, same as [BaseUrl].
 *
 * This is a third-party backend, not our own - [NetworkModule] builds its `Retrofit` via
 * [RetrofitFactory] with `authInterceptor = null`, so [AuthInterceptor] is never even part of
 * the client for this base URL, let alone attaches our app's bearer token.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PartnerBaseUrl
