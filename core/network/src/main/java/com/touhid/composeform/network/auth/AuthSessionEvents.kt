package com.touhid.composeform.network.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * How `:network` tells the app the user's session is gone for good (the refresh token is
 * missing, or the backend rejected it) - by then [TokenAuthenticator] has already cleared both
 * tokens via [TokenProvider.clear]. What happens next (navigating to a sign-in screen, signing
 * out of other SDKs, ...) is the consuming module's concern, same deferral pattern as
 * [TokenProvider]: inject this and collect [sessionExpired].
 */
@Singleton
class AuthSessionEvents @Inject internal constructor() {

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    internal fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}
