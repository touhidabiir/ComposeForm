package com.touhid.composeform.feature.webview.navigation

import com.touhid.composeform.navigation.Destination

// A web page shown inside the app (privacy policy, terms, help pages...), not in an external
// browser. url must be http(s); anything else is refused and the screen closes. title is shown in
// the top bar; null shows the page's own title once it loads.
data class WebPageDestination(
    val url: String,
    val title: String? = null,
) : Destination
