package com.touhid.composeform.di

import com.touhid.composeform.BuildConfig
import com.touhid.composeform.network.BaseUrl
import com.touhid.composeform.network.DebugMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = "https://api.composeform.dummy/"

    @Provides
    @DebugMode
    fun provideDebugMode(): Boolean = BuildConfig.DEBUG
}
