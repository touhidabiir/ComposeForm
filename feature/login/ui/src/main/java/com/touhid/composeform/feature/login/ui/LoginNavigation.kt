package com.touhid.composeform.feature.login.ui

import android.content.Context
import android.content.Intent
import com.touhid.composeform.feature.login.navigation.LoginIntentFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

// Backs OpenLogin with LoginActivity. Only this module knows sign-in is that Activity.
internal class LoginActivityIntentFactory @Inject constructor() : LoginIntentFactory {
    override fun create(context: Context): Intent = LoginActivity.intent(context)
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LoginNavigationModule {

    @Binds
    abstract fun bindLoginIntentFactory(factory: LoginActivityIntentFactory): LoginIntentFactory
}
