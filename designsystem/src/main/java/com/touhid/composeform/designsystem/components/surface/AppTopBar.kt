package com.touhid.composeform.designsystem.components.surface

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle

data class AppTopBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    titleOverride: AppTextOverride = AppTextOverride(),
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String = "Navigate back",
    onNavigationClick: () -> Unit = {},
    actions: List<AppTopBarAction> = emptyList(),
) {
    TopAppBar(
        modifier = modifier,
        title = { AppText(text = title, style = AppTextStyle.TitleMedium, override = titleOverride) },
        navigationIcon = {
            navigationIcon?.let {
                AppIconButton(
                    icon = it,
                    contentDescription = navigationIconContentDescription,
                    onClick = onNavigationClick,
                )
            }
        },
        actions = {
            actions.forEach { action ->
                AppIconButton(
                    icon = action.icon,
                    contentDescription = action.contentDescription,
                    onClick = action.onClick,
                )
            }
        },
    )
}
