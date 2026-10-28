package com.touhid.composeform.feature.lead.data

import com.touhid.composeform.feature.lead.domain.LeadRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LeadDataModule {

    @Binds
    abstract fun bindLeadRepository(impl: LeadRepositoryImpl): LeadRepository

    companion object {
        // The unqualified Retrofit is :core:network's main @BaseUrl client (NetworkModule.provideRetrofit).
        @Provides
        @Singleton
        fun provideLeadApi(retrofit: Retrofit): LeadApi = retrofit.create(LeadApi::class.java)
    }
}
