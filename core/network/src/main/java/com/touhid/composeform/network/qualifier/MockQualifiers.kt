package com.touhid.composeform.network.qualifier

import javax.inject.Qualifier

/**
 * The interceptor that answers requests with mocked responses. Provided by `:core:network-mock`,
 * which only the flavors that may serve mocks include, so in the others (prod) this binding is
 * absent and `:core:network` sees an empty `Optional`.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockInterceptor

/**
 * Whether requests are answered by the [MockInterceptor]. Bound by the app module from its
 * flavor's `BuildConfig.IS_MOCK_ENABLED`, the same way it binds [BaseUrl].
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockEnabled
