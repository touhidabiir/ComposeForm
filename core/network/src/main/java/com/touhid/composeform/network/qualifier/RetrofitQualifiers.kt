package com.touhid.composeform.network.qualifier

import javax.inject.Qualifier

// The refresh-token endpoint lives on the main @BaseUrl backend but deliberately gets its own
// Retrofit - no AuthInterceptor, no TokenAuthenticator - so TokenAuthenticator's own refresh call
// can never recurse back into itself.
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class RefreshRetrofit
