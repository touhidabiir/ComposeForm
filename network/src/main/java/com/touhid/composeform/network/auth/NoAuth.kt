package com.touhid.composeform.network.auth

/**
 * Marks a Retrofit service method whose requests must never carry the app's bearer token - e.g.
 * a partner/third-party endpoint that isn't this app's own backend. [AuthInterceptor] reads this
 * off Retrofit's own `Invocation` request tag, so the opt-out lives on the method declaration
 * itself rather than at each call site, where it could be forgotten.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoAuth
