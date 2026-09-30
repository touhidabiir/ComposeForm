package com.touhid.composeform.feature.acquisition

private val BengaliDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

// Used by both acquisition screens (the detail screen's score/photo counters, the list card's
// rejection count) - internal to this module rather than in :common, since :feature:leaddashboard
// renders its numbers in plain ASCII digits and has no use for it.
internal fun String.toBengaliDigits(): String = map { c -> if (c in '0'..'9') BengaliDigits[c - '0'] else c }.joinToString("")
