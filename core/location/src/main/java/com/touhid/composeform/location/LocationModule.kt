package com.touhid.composeform.location

import dagger.Binds
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(impl: FusedLocationTracker): LocationTracker
}

// How rememberLocationRequester, a composable, gets the singleton LocationTracker.
@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface LocationEntryPoint {
    fun locationTracker(): LocationTracker
}
