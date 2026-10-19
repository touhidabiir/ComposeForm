package com.touhid.composeform.feature.acquisition.data

import com.touhid.composeform.feature.acquisition.domain.AcquisitionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AcquisitionDataModule {

    @Binds
    abstract fun bindAcquisitionRepository(impl: AcquisitionRepositoryImpl): AcquisitionRepository

    companion object {
        // The unqualified Retrofit is :core:network's main @BaseUrl client (NetworkModule.provideRetrofit).
        @Provides
        @Singleton
        fun provideAcquisitionApi(retrofit: Retrofit): AcquisitionApi = retrofit.create(AcquisitionApi::class.java)
    }
}
