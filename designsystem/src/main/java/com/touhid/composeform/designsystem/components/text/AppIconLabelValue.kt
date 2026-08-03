package com.touhid.composeform.designsystem.components.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.theme.AppSpacing

enum class AppIconPosition { Start, End, Top, Bottom }

// A value on its own (icon + value) when [label] is null, or a caption stacked above the value
// when it's set - covers both the compact rows in a list card and the label/value pairs in a
// detail screen with one component, rather than two near-identical ones. [iconPosition] decides
// which side of the label/value block [icon] sits on - Start/End keep it a Row (like a leading/
// trailing compound drawable), Top/Bottom switch the outer container to a Column. [subValue], when
// set, adds a second, smaller/muted caption below the value - e.g. a secondary identifier that
// belongs to the same value but doesn't deserve equal visual weight.
@Composable
fun AppIconLabelValue(
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: (@Composable () -> Unit)? = null,
    iconPosition: AppIconPosition = AppIconPosition.Start,
    labelOverride: AppTextOverride = AppTextOverride(),
    valueOverride: AppTextOverride = AppTextOverride(),
    subValue: String? = null,
    subValueOverride: AppTextOverride = AppTextOverride(),
) {
    val textBlock: @Composable () -> Unit = {
        Column {
            label?.let {
                AppText(
                    text = it,
                    style = AppTextStyle.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    override = labelOverride,
                )
            }
            AppText(text = value, style = AppTextStyle.BodyMedium, override = valueOverride)
            subValue?.let {
                AppText(
                    text = it,
                    style = AppTextStyle.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    override = subValueOverride,
                )
            }
        }
    }

    when (iconPosition) {
        AppIconPosition.Start, AppIconPosition.End -> {
            Row(
                modifier = modifier,
                verticalAlignment = if (label != null) Alignment.Top else Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                if (iconPosition == AppIconPosition.Start) icon?.invoke()
                textBlock()
                if (iconPosition == AppIconPosition.End) icon?.invoke()
            }
        }
        AppIconPosition.Top, AppIconPosition.Bottom -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                if (iconPosition == AppIconPosition.Top) icon?.invoke()
                textBlock()
                if (iconPosition == AppIconPosition.Bottom) icon?.invoke()
            }
        }
    }
}
