package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.touhid.composeform.designsystem.theme.AppSpacing

// A blocking, non-dismissable spinner overlay for a reload that already has content behind it
// worth protecting from interaction (e.g. a retry) - distinct from pagination's inline "loading
// more" row, from AppPullToRefreshBox's own indicator, and from AppProgressIndicator (the
// non-blocking inline spinner used for a screen's very first load, when there is no detail
// content worth protecting from interaction yet; the screen remains navigable via its Back
// action).
@Composable
fun AppProgressDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Surface(shape = RoundedCornerShape(AppSpacing.Small)) {
            Box(modifier = Modifier.padding(AppSpacing.Large), contentAlignment = Alignment.Center) {
                AppProgressIndicator()
            }
        }
    }
}
