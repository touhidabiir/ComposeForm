package com.touhid.composeform.network

import javax.inject.Qualifier

/**
 * Binds the Retrofit base URL. Consuming modules provide this via their own
 * Hilt module, e.g. `@Provides @BaseUrl fun provideBaseUrl(): String = "https://..."`.
 * Retrofit requires the value to end with a trailing slash (e.g. `https://api.example.com/`,
 * not `https://api.example.com`) - [NetworkModule] normalizes a missing one, but supply it
 * correctly to avoid surprises if the value is ever used outside [NetworkModule].
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl
