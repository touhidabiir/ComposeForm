package com.touhid.composeform.network

import javax.inject.Qualifier

/**
 * Binds whether request/response bodies should be logged. Consuming modules provide this
 * via their own Hilt module, e.g. `@Provides @DebugMode fun provideDebugMode(): Boolean = BuildConfig.DEBUG`.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DebugMode
