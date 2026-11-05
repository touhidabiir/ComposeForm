package com.touhid.composeform.feature.webview.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme

// Shows one web page inside the app. Opened only through WebPageDestination (see
// WebPageNavigation.kt), from any feature. No Hilt: it injects nothing.
class WebPageActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(EXTRA_URL)
        if (url == null || !isWebUrl(url)) {
            // Only http(s) pages: never file:, content:, javascript: or intent: URLs.
            finish()
            return
        }
        val title = intent.getStringExtra(EXTRA_TITLE)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                WebPageScreen(url = url, title = title, onClose = ::finish)
            }
        }
    }

    companion object {
        private const val EXTRA_URL = "url"
        private const val EXTRA_TITLE = "title"

        fun intent(context: Context, url: String, title: String?): Intent =
            Intent(context, WebPageActivity::class.java)
                .putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_TITLE, title)
    }
}

internal fun isWebUrl(url: String): Boolean =
    Uri.parse(url).scheme?.lowercase() in setOf("http", "https")
