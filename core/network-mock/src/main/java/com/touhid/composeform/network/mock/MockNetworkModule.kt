package com.touhid.composeform.network.mock

import com.touhid.composeform.network.qualifier.MockInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor

// Fills :core:network's optional @MockInterceptor binding. Present only in the flavors whose
// :app depends on this module; :core:network adds it to the client only when @MockEnabled is true.
@Module
@InstallIn(SingletonComponent::class)
internal object MockNetworkModule {

    @Provides
    @MockInterceptor
    fun provideMockInterceptor(): Interceptor = MockDataInterceptor()
}
