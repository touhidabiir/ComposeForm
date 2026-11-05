package com.touhid.composeform.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.touhid.composeform.R
import com.touhid.composeform.designsystem.components.button.AppButton

// Home for UserType.Approver: acquisition approvals only.
@Composable
fun ApproverHomeScreen(
    onOpenAcquisitionApproval: () -> Unit,
    onLogout: () -> Unit,
) {
    HomeScaffold(onLogout = onLogout) {
        AppButton(
            text = stringResource(R.string.home_acquisition_approval),
            onClick = onOpenAcquisitionApproval,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
