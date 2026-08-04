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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
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
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusNeutral
import com.touhid.composeform.designsystem.theme.StatusSuccess
import com.touhid.composeform.designsystem.theme.StatusWarning

private val RowIconSize = 16.dp
private val CopyIconSize = 14.dp
private const val MaxPremiumnessScore = 100

// Same brand secondary accent as the list screens - one caller here too, kept local rather than
// promoted into :designsystem's theme (see LeadDashboardScreen.kt for the fuller rationale).
private val AccentIndigo = Color(0xFF675C92)

private val copyIcon: @Composable () -> Unit = {
    AppIcon(
        icon = Icons.Filled.ContentCopy,
        contentDescription = "Copy",
        modifier = Modifier.size(CopyIconSize),
        tint = BrandPrimary,
    )
}

private fun Boolean.toBengaliYesNo(): String = if (this) "হ্যাঁ" else "না"

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
                title = detail.shopName,
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
                label = "ওয়ালেট নম্বর",
                value = detail.walletNumber,
                icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize), tint = AccentIndigo) },
                trailingIcon = copyIcon,
            )

            ScoreSection(score = detail.premiumnessScore, band = detail.premiumnessBand)

            // detail.images.shopImageOutside/shopImageInside/businessProofImage hold the real
            // URLs - still rendered as color placeholders since no image-loading library
            // (e.g. Coil) is wired into :app yet; see CLAUDE.md's design system boundary notes.
            PhotoBlock(caption = "আউটলেটের বাহিরের ছবি", counter = "1/3", color = PhotoPlaceholderColors[0])
            PhotoBlock(caption = "আউটলেটের ভিতরের ছবি", counter = "2/3", color = PhotoPlaceholderColors[1])
            PhotoBlock(caption = "ব্যবসার পরিচয়পত্রের ছবি", counter = "3/3", color = PhotoPlaceholderColors[2])

            AppCard(modifier = Modifier.fillMaxWidth()) {
                AppText(text = "Owner & Contact Person Details", style = AppTextStyle.TitleMedium)
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppIconLabelValue(label = "Outlet Owner Info", value = detail.contactInfo.outletOwner.name)
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                AppIconLabelValue(
                    value = detail.contactInfo.outletOwner.phoneNumber,
                    icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize), tint = AccentIndigo) },
                    trailingIcon = copyIcon,
                )
                AppDivider(modifier = Modifier.padding(vertical = AppSpacing.Medium))
                AppIconLabelValue(
                    label = "Contact Person Info",
                    value = detail.contactInfo.contactPerson.designation?.let { "${detail.contactInfo.contactPerson.name} ($it)" }
                        ?: detail.contactInfo.contactPerson.name,
                )
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                AppIconLabelValue(
                    value = detail.contactInfo.contactPerson.phoneNumber,
                    icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize), tint = AccentIndigo) },
                    trailingIcon = copyIcon,
                )
            }

            LabeledPairCard(title = "Outlet Information", pairs = detail.outletInfo.toLabeledPairs())
            DigitalPaymentCard(digitalPayment = detail.digitalPayment)
            LabeledPairCard(title = "Wallet Information", pairs = detail.walletInfo.toLabeledPairs())
            LabeledPairCard(title = "Survey Responses", pairs = detail.surveyResponses.map { it.toLabeledPair() })

            AppCard(modifier = Modifier.fillMaxWidth()) {
                AppText(text = "Submission Info", style = AppTextStyle.TitleMedium)
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppIconLabelValue(
                    label = "লিড ক্লোজার এ. টি. ও.",
                    value = "${detail.audit.submittedBy.name} (${detail.audit.submittedBy.employeeId})",
                    icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(RowIconSize), tint = AccentIndigo) },
                    trailingIcon = copyIcon,
                    subValue = "এম. এ.- ${detail.audit.submittedBy.servingMa}",
                )
            }
        }
    }
}

private fun OutletInfo.toLabeledPairs(): List<LabeledPair> = listOf(
    LabeledPair("Address", address),
    LabeledPair("District", district),
    LabeledPair("Thana", thana),
    LabeledPair("Market Name", marketName),
    LabeledPair("BMCC Code", bmccCode),
    LabeledPair("BMCC Name", bmccName),
    LabeledPair("Product Type", productType),
    LabeledPair("Outlet Location Type", outletLocationType),
    LabeledPair("Outlet Type", outletType),
)

private fun WalletInfo.toLabeledPairs(): List<LabeledPair> = listOf(
    LabeledPair("Proposed Wallet Number", proposedWalletNumber),
    LabeledPair("SIM Stays At Outlet?", simStaysAtOutlet.toBengaliYesNo()),
    LabeledPair("SIM Used In Smartphone?", simUsedInSmartphone.toBengaliYesNo()),
    LabeledPair("SIM Owned By Shop Owner?", simOwnedByShopOwner.toBengaliYesNo()),
)

private fun SurveyResponse.toLabeledPair(): LabeledPair =
    LabeledPair(question, if (points != null) "$answer  •  $points pts" else answer)

// The score bands (label, range, and color) are acquisition-scoring business data, not a generic
// design-system concept - kept here rather than baked into a designsystem component, built
// entirely from already-exposed pieces (AppText, AppIcon) plus Foundation layout, so there's no
// Material3-wrapping reason for it to live anywhere else. The active band comes straight from the
// API's own premiumness_band classification rather than being re-derived from score/ratio here.
private data class ScoreBand(val label: String, val range: String, val color: Color)

private val ScoreBands = listOf(
    ScoreBand("খুব ঝুঁকিপূর্ণ", "0-24", StatusError),
    ScoreBand("ঝুঁকিপূর্ণ", "25-49", Color(0xFFFF8F00)),
    ScoreBand("মাঝারি", "50-74", StatusWarning),
    ScoreBand("ভালো", "75-89", Color(0xFFAEEA00)),
    ScoreBand("খুব ভালো", "90-100", StatusSuccess),
)

private val PhotoPlaceholderColors = listOf(Color(0xFFB0BEC5), Color(0xFF90A4AE), Color(0xFFCFD8DC))

@Composable
private fun ScoreSection(score: Double, band: String) {
    val activeIndex = ScoreBands.indexOfFirst { it.range == band }.takeIf { it >= 0 } ?: ScoreBands.size / 2
    val tierColor = ScoreBands[activeIndex].color
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        ScoreCircle(title = "স্কোর", score = score, maxScore = MaxPremiumnessScore, color = tierColor)
        ScoreBandIndicator(activeIndex = activeIndex, modifier = Modifier.fillMaxWidth())
        AppStepperButton(label = "বিস্তারিত দেখুন", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

// A one-off view for this screen only - no Material3-derived default color to justify a
// :designsystem home (unlike AppStatusBadge, this has exactly one caller and always receives an
// explicit color), so it's built directly here from plain Foundation border()/CircleShape.
@Composable
private fun ScoreCircle(title: String, score: Double, maxScore: Int, color: Color, modifier: Modifier = Modifier) {
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
                    text = "%.1f".format(score),
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

@Composable
private fun DigitalPaymentCard(digitalPayment: DigitalPayment) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        AppText(text = "Digital Payment", style = AppTextStyle.TitleMedium)
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        AppIconLabelValue(label = "Card Payment Available", value = digitalPayment.cardPaymentAvailable.toBengaliYesNo())
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "Other MFS Available", value = digitalPayment.otherMfsAvailable.toBengaliYesNo())
        if (digitalPayment.facilities.isNotEmpty()) {
            AppDivider(modifier = Modifier.padding(vertical = AppSpacing.Medium))
            digitalPayment.facilities.forEachIndexed { index, facility ->
                FacilityRow(facility = facility)
                if (index != digitalPayment.facilities.lastIndex) {
                    Spacer(modifier = Modifier.height(AppSpacing.Small))
                }
            }
        }
    }
}

@Composable
private fun FacilityRow(facility: Facility) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
    ) {
        AppIcon(
            icon = if (facility.completed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            modifier = Modifier.size(RowIconSize),
            tint = if (facility.completed) StatusSuccess else StatusError,
        )
        AppText(text = facility.name, style = AppTextStyle.BodyMedium)
    }
}
