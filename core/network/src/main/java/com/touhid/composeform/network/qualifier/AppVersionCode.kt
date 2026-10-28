package com.touhid.composeform.network.qualifier

import javax.inject.Qualifier

/**
 * The app's `versionCode`, sent on every request as the `X-Version` header (see
 * `AppVersionInterceptor`). Library modules have no version code of their own, so the app module
 * binds it from its own `BuildConfig`, the same way it binds [BaseUrl]:
 * `@Provides @AppVersionCode fun provideAppVersionCode(): Int = BuildConfig.VERSION_CODE`.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppVersionCode
