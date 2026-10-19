package com.touhid.composeform.feature.specificform.data

import com.touhid.composeform.feature.specificform.domain.SpecificFormRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SpecificFormDataModule {

    @Binds
    abstract fun bindSpecificFormRepository(impl: SpecificFormRepositoryImpl): SpecificFormRepository

    companion object {
        // The unqualified Retrofit is :core:network's main @BaseUrl client (NetworkModule.provideRetrofit).
        @Provides
        @Singleton
        fun provideSpecificFormApi(retrofit: Retrofit): SpecificFormApi = retrofit.create(SpecificFormApi::class.java)
    }
}
