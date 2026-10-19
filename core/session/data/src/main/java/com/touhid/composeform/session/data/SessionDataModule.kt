package com.touhid.composeform.session.data

import com.touhid.composeform.session.domain.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SessionDataModule {

    @Binds
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    companion object {
        // The unqualified Retrofit is :core:network's main @BaseUrl client (NetworkModule.provideRetrofit).
        @Provides
        @Singleton
        fun provideSessionApi(retrofit: Retrofit): SessionApi = retrofit.create(SessionApi::class.java)
    }
}
