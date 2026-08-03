package com.touhid.composeform.acquisition

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppButtonTone
import com.touhid.composeform.designsystem.components.button.AppOutlinedButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.indicator.AppGradientRangeIndicator
import com.touhid.composeform.designsystem.components.indicator.AppScoreGauge
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppBottomActionBar
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppDivider
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppIconLabelValue
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing

private val RowIconSize = 16.dp
private val PhotoPlaceholderColors = listOf(Color(0xFFB0BEC5), Color(0xFF90A4AE), Color(0xFFCFD8DC))

@Composable
fun AcquisitionApprovalDetailScreen(
    onBack: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
    detail: AcquisitionDetail = remember { sampleAcquisitionDetail() },
) {
    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { scrollBehavior ->
            AppTopBar(
                title = detail.merchantName,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            AppBottomActionBar {
                AppOutlinedButton(
                    text = "Reject",
                    onClick = onReject,
                    tone = AppButtonTone.Danger,
                    modifier = Modifier.weight(1f),
                )
                AppButton(
                    text = "Approve",
                    onClick = onApprove,
                    tone = AppButtonTone.Success,
                    modifier = Modifier.weight(1f),
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        ) {
            AppIconLabelValue(
                value = detail.phoneNumber,
                icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
            )

            ScoreSection(score = detail.score, maxScore = detail.maxScore)

            detail.photoCaptions.forEachIndexed { index, caption ->
                PhotoBlock(
                    caption = caption,
                    counter = "${index + 1}/${detail.photoCaptions.size}",
                    color = PhotoPlaceholderColors[index % PhotoPlaceholderColors.size],
                )
            }

            AppCard(modifier = Modifier.fillMaxWidth()) {
                AppText(text = "Owner & Contact Person Details", style = AppTextStyle.TitleMedium)
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppIconLabelValue(label = "Shop Owner Info", value = detail.shopOwner.name)
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                AppIconLabelValue(
                    value = detail.shopOwner.idOrPhone,
                    icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
                )
                AppDivider(modifier = Modifier.padding(vertical = AppSpacing.Medium))
                AppIconLabelValue(label = "Shop Operator Info", value = detail.shopOperator.name)
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                AppIconLabelValue(
                    value = detail.shopOperator.idOrPhone,
                    icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
                )
            }

            LabeledPairCard(title = "Outlet Information", pairs = detail.outletInfo)
            LabeledPairCard(title = "Wallet Information", pairs = detail.walletInfo)
        }
    }
}

@Composable
private fun ScoreSection(score: Int, maxScore: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        AppScoreGauge(score = score, maxScore = maxScore)
        AppGradientRangeIndicator(
            value = if (maxScore > 0) score.toFloat() / maxScore.toFloat() else 0f,
            modifier = Modifier.fillMaxWidth(),
        )
        AppStepperButton(label = "বিস্তারিত দেখুন", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

// A single outlet photo, stacked in a vertical list with the others (not a swipeable carousel -
// all photos are visible at once in this screen) - caption bottom-left, "n/total" bottom-right.
@Composable
private fun PhotoBlock(caption: String, counter: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = ColorPainter(color),
            contentDescription = caption,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(AppSpacing.Small)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppText(text = caption, style = AppTextStyle.BodyMedium)
            AppText(text = counter, style = AppTextStyle.Label)
        }
    }
}

@Composable
private fun LabeledPairCard(title: String, pairs: List<LabeledPair>) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        AppText(text = title, style = AppTextStyle.TitleMedium)
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        pairs.forEachIndexed { index, pair ->
            AppIconLabelValue(label = pair.label, value = pair.value)
            if (index != pairs.lastIndex) {
                Spacer(modifier = Modifier.height(AppSpacing.Small))
            }
        }
    }
}
