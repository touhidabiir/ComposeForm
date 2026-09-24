package com.touhid.composeform.data.di

import com.touhid.composeform.data.repository.AnalyticsRepository
import com.touhid.composeform.data.repository.AppRepository
import com.touhid.composeform.data.repository.PartnerRepository
import com.touhid.composeform.data.repository.PaymentRepository
import com.touhid.composeform.data.repository.impl.AnalyticsRepositoryImpl
import com.touhid.composeform.data.repository.impl.AppRepositoryImpl
import com.touhid.composeform.data.repository.impl.PartnerRepositoryImpl
import com.touhid.composeform.data.repository.impl.PaymentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

    @Binds
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    abstract fun bindAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository

    @Binds
    abstract fun bindPartnerRepository(impl: PartnerRepositoryImpl): PartnerRepository
}
