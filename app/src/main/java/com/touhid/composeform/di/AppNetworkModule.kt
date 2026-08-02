package com.touhid.composeform.di

import com.touhid.composeform.network.BaseUrl
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
}
