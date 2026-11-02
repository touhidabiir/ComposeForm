package com.touhid.composeform.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.session.SessionEndReason
import com.touhid.composeform.session.domain.SessionRepository
import com.touhid.composeform.session.domain.SessionState
import com.touhid.composeform.session.domain.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface SplashTarget {
    // Signed in: show this user type's home.
    data class Home(val userType: UserType) : SplashTarget

    // Not signed in: open sign-in. sessionExpired: tell the user why first.
    data class Login(val sessionExpired: Boolean) : SplashTarget
}

// Where the app goes from the splash - the one place that decides it, both on launch and once
// sign-in returns. Lives in the ViewModel so a rotation during the splash doesn't restart it.
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _target = MutableStateFlow<SplashTarget?>(null)
    val target = _target.asStateFlow()

    private var started = false

    // Called once by the splash. endReason is set when SessionGuard restarted the app because a
    // session ended: then there's no reason to wait on the splash.
    fun start(endReason: SessionEndReason?) {
        if (started) return
        started = true
        viewModelScope.launch {
            val target = async { resolve(expired = endReason == SessionEndReason.Expired) }
            if (endReason == null) delay(SplashDuration)
            _target.value = target.await()
        }
    }

    // Sign-in returned signed in: the session is stored now.
    fun onSignedIn() {
        viewModelScope.launch { _target.value = resolve(expired = false) }
    }

    private suspend fun resolve(expired: Boolean): SplashTarget = when (val state = sessionRepository.state()) {
        is SessionState.SignedIn -> SplashTarget.Home(state.user.userType)
        SessionState.Ended -> {
            // Signed in on an earlier day, or the refresh token was rejected while the app was away.
            sessionRepository.signOut()
            SplashTarget.Login(sessionExpired = true)
        }
        SessionState.SignedOut -> SplashTarget.Login(sessionExpired = expired)
    }

    private companion object {
        val SplashDuration = 3.seconds
    }
}
