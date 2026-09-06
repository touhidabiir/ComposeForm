package com.touhid.composeform.network

import javax.inject.Qualifier

/**
 * Binds the partner service's Retrofit base URL - same deferral pattern as [BaseUrl]: a
 * consuming module's own Hilt module provides the value (e.g.
 * `@Provides @PartnerBaseUrl fun providePartnerBaseUrl(): String = "https://..."`). Requires a
 * trailing slash, same as [BaseUrl].
 *
 * This is a third-party backend, not our own - [com.touhid.composeform.network.api.PartnerApiService]'s
 * methods are annotated `@NoAuth` so [com.touhid.composeform.network.auth.AuthInterceptor] never
 * attaches our app's bearer token to requests against this base URL.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PartnerBaseUrl
