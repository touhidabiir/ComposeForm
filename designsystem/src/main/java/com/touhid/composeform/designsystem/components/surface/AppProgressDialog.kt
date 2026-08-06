package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary

// A blocking, non-dismissable spinner overlay for an API call with nothing else meaningful to
// show yet (initial load, filter switch, search submit, retry) - distinct from pagination's
// inline "loading more" row and from AppPullToRefreshBox's own indicator, neither of which should
// compete with a full-screen dialog.
@Composable
fun AppProgressDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Surface(shape = RoundedCornerShape(AppSpacing.Small)) {
            Box(modifier = Modifier.padding(AppSpacing.Large), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandPrimary)
            }
        }
    }
}
