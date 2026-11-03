package com.touhid.composeform.session

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.touhid.composeform.MainActivity
import com.touhid.composeform.network.auth.AuthSessionEvents
import com.touhid.composeform.session.domain.SessionRepository
import com.touhid.composeform.session.domain.SessionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// Why the app was restarted at the splash: MainActivity reads it from EXTRA_SESSION_END.
enum class SessionEndReason { SignedOut, Expired }

// Every way a session ends goes through here, and always ends the same way: the session is
// cleared and the app restarts at MainActivity on a fresh task. Whatever screens were open are
// gone, and the splash then does what it always does - no session, so sign-in, then the home for
// whoever signs in. No other place has to know how to get from "signed out" to the right home.
//
// A session ends when:
// - the user taps Log out on a home (signOut);
// - the refresh token is rejected: TokenAuthenticator has cleared the tokens and says so on
//   AuthSessionEvents;
// - the app comes to the foreground and the session is no longer valid (SessionState.Ended): it
//   started on an earlier day, or the refresh token was rejected while the app was in the
//   background, where Android doesn't let an app start an Activity.
// While nobody is signed in (e.g. mid sign-in) the state is SignedOut, so nothing here interrupts
// the sign-in screens.
@Singleton
class SessionGuard @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository,
    private val authSessionEvents: AuthSessionEvents,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Called once, from ComposeFormApplication.onCreate.
    fun start() {
        scope.launch {
            authSessionEvents.sessionExpired.collect {
                // In the background, leave it: the user is still stored, so the state reads Ended
                // and the next ON_START ends the session. Checking Ended also makes a second
                // event (parallel requests) a no-op once the first has signed out.
                if (isInForeground() && sessionRepository.state() == SessionState.Ended) {
                    endSession(SessionEndReason.Expired)
                }
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    scope.launch {
                        if (sessionRepository.state() == SessionState.Ended) endSession(SessionEndReason.Expired)
                    }
                }
            },
        )
    }

    // The Log out button on either home.
    fun signOut() {
        scope.launch { endSession(SessionEndReason.SignedOut) }
    }

    private suspend fun endSession(reason: SessionEndReason) {
        sessionRepository.signOut()
        context.startActivity(
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra(EXTRA_SESSION_END, reason.name),
        )
    }

    private fun isInForeground(): Boolean =
        ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    companion object {
        const val EXTRA_SESSION_END = "sessionEnd"
    }
}
