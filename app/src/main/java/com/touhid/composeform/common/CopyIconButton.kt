package com.touhid.composeform.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.AnnotatedString
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.theme.BrandPrimary
import kotlinx.coroutines.launch

// Shared by every row that offers a "copy to clipboard" trailing icon (Lead Dashboard,
// Acquisition List, Acquisition Detail) - a function (not a fixed composable) since each row
// copies different text; reads LocalClipboard/rememberCoroutineScope once per call so the
// returned lambda's onClick can write straight to the system clipboard. Uses LocalClipboard
// (Clipboard.setClipEntry) rather than the deprecated LocalClipboardManager (ClipboardManager.
// setText) - setClipEntry is suspend, hence the coroutine scope.
@Composable
fun copyIconButton(text: String): @Composable () -> Unit {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    return {
        AppIconButton(
            icon = Icons.Filled.ContentCopy,
            contentDescription = "Copy",
            onClick = { scope.launch { clipboard.setClipEntry(AnnotatedString(text).toClipEntry()) } },
            tint = BrandPrimary,
        )
    }
}
