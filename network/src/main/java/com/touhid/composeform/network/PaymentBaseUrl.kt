package com.touhid.composeform.network

import javax.inject.Qualifier

/**
 * Binds the payment service's Retrofit base URL - same deferral pattern as [BaseUrl]: a
 * consuming module's own Hilt module provides the value (e.g.
 * `@Provides @PaymentBaseUrl fun providePaymentBaseUrl(): String = "https://..."`). Requires a
 * trailing slash, same as [BaseUrl].
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentBaseUrl
