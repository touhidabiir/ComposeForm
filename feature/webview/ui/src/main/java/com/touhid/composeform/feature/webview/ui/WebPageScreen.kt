package com.touhid.composeform.feature.webview.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.touhid.composeform.common.R as CommonR
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppLinearProgressIndicator
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.AppSpacing

// One web page in a WebView, under a top bar with Close. Back goes back through the pages the user
// followed, then closes. A thin bar shows loading progress; a failed load shows an error with Retry.
// Links stay in the WebView while they're http(s); anything else (tel:, mailto:, a store link...)
// is handed to the app that handles it.
@SuppressLint("SetJavaScriptEnabled") // Many pages need JS; no JavaScript interface is exposed.
@Composable
internal fun WebPageScreen(url: String, title: String?, onClose: () -> Unit) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var loading by remember { mutableStateOf(true) }
    var failed by remember { mutableStateOf(false) }
    var pageTitle by remember { mutableStateOf(title) }

    BackHandler {
        val current = webView
        if (current != null && current.canGoBack()) current.goBack() else onClose()
    }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = pageTitle.orEmpty(),
                navigationIcon = Icons.Filled.Close,
                navigationIconContentDescription = stringResource(CommonR.string.common_close),
                onNavigationClick = onClose,
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (loading) {
                AppLinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                )
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            // No local files or content providers: this only shows web pages.
                            settings.allowFileAccess = false
                            settings.allowContentAccess = false
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                    if (isWebUrl(request.url.toString())) return false
                                    try {
                                        view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                                    } catch (_: ActivityNotFoundException) {
                                        // Nothing on the device handles it: ignore the tap.
                                    }
                                    return true
                                }

                                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                                    loading = true
                                }

                                override fun onPageFinished(view: WebView, url: String?) {
                                    loading = false
                                }

                                override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                                    // Only the page itself; a broken image or script inside it isn't worth an error screen.
                                    if (request.isForMainFrame) failed = true
                                }
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView, newProgress: Int) {
                                    progress = newProgress / 100f
                                }

                                override fun onReceivedTitle(view: WebView, receivedTitle: String?) {
                                    // A title the caller passed wins over the page's own.
                                    if (title == null && !receivedTitle.isNullOrBlank()) pageTitle = receivedTitle
                                }
                            }
                            loadUrl(url)
                            webView = this
                        }
                    },
                    onRelease = { it.destroy() },
                )

                if (failed) {
                    LoadError(
                        onRetry = {
                            failed = false
                            webView?.reload()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadError(onRetry: () -> Unit) {
    // Covers the WebView's own error page.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(AppSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppText(text = stringResource(R.string.webview_load_error), textAlign = TextAlign.Center)
        AppText(text = stringResource(CommonR.string.common_retry_message), textAlign = TextAlign.Center)
        AppButton(text = stringResource(CommonR.string.common_retry_action), onClick = onRetry)
    }
}
