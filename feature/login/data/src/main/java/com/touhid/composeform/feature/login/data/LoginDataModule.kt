package com.touhid.composeform.feature.login.data

import com.touhid.composeform.feature.login.domain.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LoginDataModule {

    @Binds
    abstract fun bindLoginRepository(impl: LoginRepositoryImpl): LoginRepository

    companion object {
        // The unqualified Retrofit is :core:network's main @BaseUrl client (NetworkModule.provideRetrofit).
        @Provides
        @Singleton
        fun provideLoginApi(retrofit: Retrofit): LoginApi = retrofit.create(LoginApi::class.java)
    }
}
