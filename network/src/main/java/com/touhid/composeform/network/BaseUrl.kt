package com.touhid.composeform.network

import javax.inject.Qualifier

/**
 * Binds the Retrofit base URL. Consuming modules provide this via their own
 * Hilt module, e.g. `@Provides @BaseUrl fun provideBaseUrl(): String = "https://..."`.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl
