package com.touhid.composeform.session

import android.content.Context
import android.content.Intent
import com.touhid.composeform.MainActivity
import com.touhid.composeform.session.domain.SessionRepository
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
@Singleton
class SessionGuard @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

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

    companion object {
        const val EXTRA_SESSION_END = "sessionEnd"
    }
}
