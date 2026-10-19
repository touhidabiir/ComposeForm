package com.touhid.composeform.common

import java.text.SimpleDateFormat
import java.util.Locale

// Formats a rejection's reviewed_at ("2026/07/01 11:00:00 AM" on the wire) for display as
// "1 Jul 2026; 11:00 AM" - shared by :feature:leaddashboard's rejection sheet and
// :feature:acquisition's previous-rejections card, which both render the same Rejection.reviewedAt
// field. Falls back to the raw string if the backend ever sends a different format, rather than
// crashing the screen over a timestamp.
fun formatRejectionTimestamp(reviewedAt: String): String = runCatching {
    val parsed = SimpleDateFormat("yyyy/MM/dd hh:mm:ss a", Locale.US).parse(reviewedAt)
    SimpleDateFormat("d MMM yyyy; h:mm a", Locale.US).format(parsed!!)
}.getOrDefault(reviewedAt)
