package com.touhid.composeform.location

// The answer to one request for the current location: a position, or why there isn't one.
// A one-shot result, not an ongoing state - each request gives a new one.
sealed interface LocationOutcome {
    data class Found(val point: GeoPoint) : LocationOutcome

    // The user hasn't granted location access.
    data object NoPermission : LocationOutcome

    // Location is switched off on the device.
    data object Disabled : LocationOutcome

    // No position in time (indoors, no signal...). Trying again may work.
    data object Unavailable : LocationOutcome
}
