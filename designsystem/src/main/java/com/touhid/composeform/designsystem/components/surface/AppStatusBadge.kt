package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusErrorContainer
import com.touhid.composeform.designsystem.theme.StatusInfo
import com.touhid.composeform.designsystem.theme.StatusInfoContainer
import com.touhid.composeform.designsystem.theme.StatusNeutral
import com.touhid.composeform.designsystem.theme.StatusNeutralContainer
import com.touhid.composeform.designsystem.theme.StatusSuccess
import com.touhid.composeform.designsystem.theme.StatusSuccessContainer
import com.touhid.composeform.designsystem.theme.StatusWarning
import com.touhid.composeform.designsystem.theme.StatusWarningContainer

enum class AppStatusTone { Success, Warning, Error, Info, Neutral }

@Composable
fun AppStatusBadge(
    text: String,
    tone: AppStatusTone,
    modifier: Modifier = Modifier,
) {
    val (dotColor, containerColor, contentColor) = when (tone) {
        AppStatusTone.Success -> Triple(StatusSuccess, StatusSuccessContainer, StatusSuccess)
        AppStatusTone.Warning -> Triple(StatusWarning, StatusWarningContainer, StatusWarning)
        AppStatusTone.Error -> Triple(StatusError, StatusErrorContainer, StatusError)
        AppStatusTone.Info -> Triple(StatusInfo, StatusInfoContainer, StatusInfo)
        AppStatusTone.Neutral -> Triple(StatusNeutral, StatusNeutralContainer, StatusNeutral)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.Small, vertical = AppSpacing.ExtraSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.ExtraSmall),
        ) {
            Box(modifier = Modifier.size(6.dp).background(color = dotColor, shape = CircleShape))
            AppText(text = text, style = AppTextStyle.Label, color = contentColor)
        }
    }
}
