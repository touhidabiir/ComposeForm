package com.touhid.composeform.data.di

import com.touhid.composeform.data.repository.AnalyticsRepository
import com.touhid.composeform.data.repository.AppRepository
import com.touhid.composeform.data.repository.DefaultAnalyticsRepository
import com.touhid.composeform.data.repository.DefaultAppRepository
import com.touhid.composeform.data.repository.DefaultPartnerRepository
import com.touhid.composeform.data.repository.DefaultPaymentRepository
import com.touhid.composeform.data.repository.PartnerRepository
import com.touhid.composeform.data.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindAppRepository(impl: DefaultAppRepository): AppRepository

    @Binds
    abstract fun bindPaymentRepository(impl: DefaultPaymentRepository): PaymentRepository

    @Binds
    abstract fun bindAnalyticsRepository(impl: DefaultAnalyticsRepository): AnalyticsRepository

    @Binds
    abstract fun bindPartnerRepository(impl: DefaultPartnerRepository): PartnerRepository
}
