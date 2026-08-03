package com.touhid.composeform.acquisition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppButtonTone
import com.touhid.composeform.designsystem.components.button.AppOutlinedButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppBottomActionBar
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppDivider
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppIconLabelValue
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusNeutral
import com.touhid.composeform.designsystem.theme.StatusSuccess
import com.touhid.composeform.designsystem.theme.StatusWarning

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

// The score bands (label, color, and how many there are) are acquisition-scoring business
// data, not a generic design-system concept - kept here rather than baked into a designsystem
// component, built entirely from already-exposed pieces (AppText, AppIcon) plus Foundation
// layout, so there's no Material3-wrapping reason for it to live anywhere else.
private data class ScoreBand(val label: String, val color: Color)

private val ScoreBands = listOf(
    ScoreBand("খুব ঝুঁকিপূর্ণ", StatusError),
    ScoreBand("ঝুঁকিপূর্ণ", Color(0xFFFF8F00)),
    ScoreBand("মাঝারি", StatusWarning),
    ScoreBand("ভালো", Color(0xFFAEEA00)),
    ScoreBand("খুব ভালো", StatusSuccess),
)

@Composable
private fun ScoreSection(score: Int, maxScore: Int) {
    val ratio = if (maxScore > 0) score.toFloat() / maxScore.toFloat() else 0f
    val activeIndex = (ratio.coerceIn(0f, 1f) * ScoreBands.size).toInt().coerceIn(0, ScoreBands.size - 1)
    val tierColor = ScoreBands[activeIndex].color
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        ScoreCircle(title = "স্কোর", score = score, maxScore = maxScore, color = tierColor)
        ScoreBandIndicator(activeIndex = activeIndex, modifier = Modifier.fillMaxWidth())
        AppStepperButton(label = "বিস্তারিত দেখুন", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

// A one-off view for this screen only - no Material3-derived default color to justify a
// :designsystem home (unlike AppStatusBadge, this has exactly one caller and always receives an
// explicit color), so it's built directly here from plain Foundation border()/CircleShape.
@Composable
private fun ScoreCircle(title: String, score: Int, maxScore: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(120.dp)
            .border(border = BorderStroke(width = 6.dp, color = color), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppText(text = title, style = AppTextStyle.Label, override = AppTextOverride(color = StatusNeutral))
            Row(verticalAlignment = Alignment.Bottom) {
                AppText(
                    text = "$score",
                    style = AppTextStyle.TitleLarge,
                    override = AppTextOverride(color = color, fontWeight = FontWeight.Bold),
                )
                AppText(
                    text = "/$maxScore",
                    style = AppTextStyle.BodyMedium,
                    override = AppTextOverride(color = StatusNeutral),
                )
            }
        }
    }
}

@Composable
private fun ScoreBandIndicator(activeIndex: Int, modifier: Modifier = Modifier, bands: List<ScoreBand> = ScoreBands) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.ExtraSmall)) {
        bands.forEachIndexed { index, band ->
            val isActive = index == activeIndex
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isActive) {
                    AppIcon(
                        icon = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = band.color,
                        modifier = Modifier.size(RowIconSize),
                    )
                } else {
                    Spacer(modifier = Modifier.size(RowIconSize))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isActive) 16.dp else 8.dp)
                        .background(color = band.color, shape = RoundedCornerShape(percent = 50)),
                )
                Spacer(modifier = Modifier.height(AppSpacing.ExtraSmall))
                AppText(
                    text = band.label,
                    style = AppTextStyle.Label,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
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
