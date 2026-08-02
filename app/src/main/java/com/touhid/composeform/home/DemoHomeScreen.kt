package com.touhid.composeform.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.theme.AppSpacing

@Composable
fun DemoHomeScreen(
    onOpenFormDemo: () -> Unit,
    onOpenLeadDashboard: () -> Unit,
    onOpenAcquisitionApproval: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { scrollBehavior -> AppTopBar(title = "ComposeForm Demo", scrollBehavior = scrollBehavior) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        ) {
            AppButton(text = "Form Builder Demo", onClick = onOpenFormDemo, modifier = Modifier.fillMaxWidth())
            AppButton(text = "Lead Dashboard", onClick = onOpenLeadDashboard, modifier = Modifier.fillMaxWidth())
            AppButton(text = "Acquisition Approval", onClick = onOpenAcquisitionApproval, modifier = Modifier.fillMaxWidth())
        }
    }
}
