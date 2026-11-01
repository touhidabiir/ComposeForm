package com.touhid.composeform.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.session.domain.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _target = MutableStateFlow<SplashTarget?>(null)
    val target = _target.asStateFlow()

    init {
        viewModelScope.launch {
            val signedIn = async { sessionRepository.isSignedIn() }
            delay(SplashDuration)
            _target.value = if (signedIn.await()) SplashTarget.Home else SplashTarget.Login
        }
    }

    private companion object {
        val SplashDuration = 3.seconds
    }
}
