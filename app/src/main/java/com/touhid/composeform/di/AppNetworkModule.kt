package com.touhid.composeform.di

import com.touhid.composeform.network.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    // TODO: replace with the real backend's base URL once one exists (per-flavor
    // BuildConfig.BASE_URL once product flavors exist - see CLAUDE.md's Network boundary notes).
    // Doesn't matter until then: MockDataInterceptor (network/) short-circuits every request this
    // URL would otherwise receive.
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = "https://api.composeform.dummy/"
}
