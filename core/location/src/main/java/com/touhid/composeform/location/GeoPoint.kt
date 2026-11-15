package com.touhid.composeform.location

// A position on Earth. accuracyMeters is the radius the real position is likely within, when the
// device reports one.
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float?,
)
