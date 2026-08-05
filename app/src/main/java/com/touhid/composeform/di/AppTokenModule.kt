package com.touhid.composeform.di

import com.touhid.composeform.network.auth.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppTokenModule {

    @Binds
    abstract fun bindTokenProvider(impl: InMemoryTokenProvider): TokenProvider
}
