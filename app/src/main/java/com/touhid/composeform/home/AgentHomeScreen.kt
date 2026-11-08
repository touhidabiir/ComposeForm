package com.touhid.composeform.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.touhid.composeform.R
import com.touhid.composeform.designsystem.components.button.AppButton

// Home for UserType.Agent: everything but acquisition approvals.
@Composable
fun AgentHomeScreen(
    onOpenFormDemo: () -> Unit,
    onOpenLeadDashboard: () -> Unit,
    onOpenSpecificForm: () -> Unit,
    onLogout: () -> Unit,
) {
    HomeScaffold(onLogout = onLogout) {
        AppButton(text = stringResource(R.string.home_form_demo), onClick = onOpenFormDemo, modifier = Modifier.fillMaxWidth())
        AppButton(text = stringResource(R.string.home_lead_dashboard), onClick = onOpenLeadDashboard, modifier = Modifier.fillMaxWidth())
        AppButton(text = stringResource(R.string.home_specific_form), onClick = onOpenSpecificForm, modifier = Modifier.fillMaxWidth())
    }
}
