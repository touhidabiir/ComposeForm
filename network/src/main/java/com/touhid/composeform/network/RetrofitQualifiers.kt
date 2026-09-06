package com.touhid.composeform.network

import javax.inject.Qualifier

// Disambiguate NetworkModule's multiple Retrofit instances (one per base URL) from each other -
// never injected outside NetworkModule, so unlike BaseUrl/PaymentBaseUrl/etc. these don't need
// a public doc/consumer contract of their own.

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class PaymentRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class AnalyticsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class PartnerRetrofit
