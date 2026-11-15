package com.touhid.composeform.location

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// Where the device is. Only reports what's possible right now: it never asks for permission or
// shows anything. A screen that needs a location uses rememberLocationRequester instead, which
// asks for permission and to turn location on first, then calls this.
interface LocationTracker {

    fun hasPermission(): Boolean

    // One position. Stops by itself once it has one or after timeout, and stops early if the
    // calling coroutine is cancelled (the GPS request is cancelled with it).
    suspend fun currentLocation(timeout: Duration = 15.seconds): LocationOutcome

    // Positions every interval while collected. Updates start when collection starts and stop
    // when it stops. Fails with SecurityException if permission is missing.
    fun updates(interval: Duration): Flow<GeoPoint>
}
