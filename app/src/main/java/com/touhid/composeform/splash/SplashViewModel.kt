package com.touhid.composeform.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.auth.TokenProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

enum class SplashTarget { Home, Login }

// Shows the splash for a fixed time, then says where to go: home when a session is stored,
// sign-in otherwise. Lives in the ViewModel so a rotation during the splash doesn't restart it.
//
// "Signed in" means an access token is stored. :app owns that storage (di/EncryptedTokenProvider),
// so it reads TokenProvider itself rather than asking a feature. An expired token still counts:
// :core:network's TokenAuthenticator refreshes it on the first 401.
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenProvider: TokenProvider,
) : ViewModel() {

    private val _target = MutableStateFlow<SplashTarget?>(null)
    val target = _target.asStateFlow()

    init {
        viewModelScope.launch {
            // EncryptedSharedPreferences can touch disk and decrypt: off the main thread.
            val signedIn = async(Dispatchers.IO) { !tokenProvider.getToken().isNullOrBlank() }
            delay(SplashDuration)
            _target.value = if (signedIn.await()) SplashTarget.Home else SplashTarget.Login
        }
    }

    private companion object {
        val SplashDuration = 3.seconds
    }
}
