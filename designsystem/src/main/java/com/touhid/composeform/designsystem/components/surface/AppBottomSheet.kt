package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.theme.AppSpacing

// A modal sheet anchored to the bottom of the screen - the Material3-wrapping reason this lives
// here, same as AppProgressDialog/AppPullToRefreshBox. Kept unopinionated (bare ColumnScope
// content, like AppCard) rather than baking in a title/close row - its one caller builds that
// itself from AppText/AppIconButton, since there's no second caller yet to justify baking in a
// fixed header shape. showDragHandle defaults to false since it's a Material3 affordance for a
// swipeable/expandable sheet - a plain content sheet like RejectionDetailsSheet doesn't want the
// extra icon and its reserved vertical space above the content.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    showDragHandle: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        dragHandle = if (showDragHandle) { { BottomSheetDefaults.DragHandle() } } else null,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium),
            content = content,
        )
    }
}
