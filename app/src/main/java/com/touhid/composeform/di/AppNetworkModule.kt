package com.touhid.composeform.di

import com.touhid.composeform.network.qualifier.AnalyticsBaseUrl
import com.touhid.composeform.network.qualifier.BaseUrl
import com.touhid.composeform.network.qualifier.PartnerBaseUrl
import com.touhid.composeform.network.qualifier.PaymentBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    // TODO: replace with the real backend's base URL once one exists (per-flavor
    // BuildConfig.BASE_URL once product flavors exist - see CLAUDE.md's Network boundary notes).
    // Doesn't matter until then: MockDataInterceptor (network/mock/) short-circuits every request
    // this URL would otherwise receive.
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = "https://api.composeform.dummy/"

    // TODO: replace with the real payment service's base URL once one exists.
    @Provides
    @PaymentBaseUrl
    fun providePaymentBaseUrl(): String = "https://payment.composeform.dummy/"

    // TODO: replace with the real analytics service's base URL once one exists.
    @Provides
    @AnalyticsBaseUrl
    fun provideAnalyticsBaseUrl(): String = "https://analytics.composeform.dummy/"

    // TODO: replace with the real partner's base URL once one exists. Third-party backend, not
    // ours - NetworkModule builds this base URL's Retrofit with authInterceptor = null, so our
    // app's bearer token is never attached here.
    @Provides
    @PartnerBaseUrl
    fun providePartnerBaseUrl(): String = "https://partner.composeform.dummy/"
}
