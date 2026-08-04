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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppButtonStyle
import com.touhid.composeform.designsystem.components.button.AppOutlinedButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppBottomActionBar
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppHorizontalDivider
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppIconLabelValue
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.StatusInfoContainer
import com.touhid.composeform.designsystem.theme.StatusNeutral

private val RowIconSize = 16.dp
private val CopyIconSize = 14.dp
private const val MaxPremiumnessScore = 100

// Same brand secondary accent as the list screens - one caller here too, kept local rather than
// promoted into :designsystem's theme (see LeadDashboardScreen.kt for the fuller rationale).
private val AccentIndigo = Color(0xFF675C92)

private val BengaliDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
private fun String.toBengaliDigits(): String = map { c -> if (c in '0'..'9') BengaliDigits[c - '0'] else c }.joinToString("")

private val copyIcon: @Composable () -> Unit = {
    AppIcon(
        icon = Icons.Filled.ContentCopy,
        contentDescription = "Copy",
        modifier = Modifier.size(CopyIconSize),
        tint = BrandPrimary,
    )
}

// The generic marker icon most Outlet/Wallet Information rows use in the design - only the
// phone and address rows get a semantically distinct icon (phone/location glyphs).
private val genericRowIcon: @Composable () -> Unit = {
    AppIcon(
        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        modifier = Modifier.size(RowIconSize),
        tint = StatusNeutral,
    )
}

private fun Boolean.toBengaliYesNo(): String = if (this) "হ্যাঁ" else "না"
private fun Boolean.toYesNo(): String = if (this) "Yes" else "No"

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
                title = "",
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
                    buttonType = AppButtonStyle.Danger,
                    modifier = Modifier.weight(1f),
                )
                AppButton(
                    text = "Approve",
                    onClick = onApprove,
                    buttonType = AppButtonStyle.Success,
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
            ShopIdentityCard(shopName = detail.shopName, walletNumber = detail.walletNumber)

            ScoreSection(score = detail.premiumnessScore, band = detail.premiumnessBand)

            // detail.images.shopImageOutside/shopImageInside/businessProofImage hold the real
            // URLs - still rendered as color placeholders since no image-loading library
            // (e.g. Coil) is wired into :app yet; see CLAUDE.md's design system boundary notes.
            PhotoBlock(caption = "আউটলেটের বাহিরের ছবি", counter = "1/3".toBengaliDigits(), color = PhotoPlaceholderColors[0])
            PhotoBlock(caption = "আউটলেটের ভিতরের ছবি", counter = "2/3".toBengaliDigits(), color = PhotoPlaceholderColors[1])
            PhotoBlock(caption = "ব্যবসার পরিচয়পত্রের ছবি", counter = "3/3".toBengaliDigits(), color = PhotoPlaceholderColors[2])

            AppCard(modifier = Modifier.fillMaxWidth()) {
                AppText(text = "Owner & Contact Person Details", style = AppTextStyle.TitleMedium)
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppIconLabelValue(
                    label = "Shop Owner Info",
                    value = detail.contactInfo.outletOwner.name,
                    icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
                )
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                AppIconLabelValue(
                    value = detail.contactInfo.outletOwner.phoneNumber,
                    icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
                    trailingIcon = copyIcon,
                )
                AppHorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.Medium))
                AppIconLabelValue(
                    label = "Shop Operator Info (if different)",
                    value = detail.contactInfo.contactPerson.name,
                    icon = { AppIcon(icon = Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
                )
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                AppIconLabelValue(
                    value = detail.contactInfo.contactPerson.phoneNumber,
                    icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
                    trailingIcon = copyIcon,
                )
            }

            OutletInformationCard(outletInfo = detail.outletInfo, digitalPayment = detail.digitalPayment)
            WalletInformationCard(walletInfo = detail.walletInfo)
        }
    }
}

@Composable
private fun ShopIdentityCard(shopName: String, walletNumber: String) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(StatusInfoContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(icon = Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.size(20.dp), tint = BrandPrimary)
            }
            Column {
                AppText(text = shopName, style = AppTextStyle.Label, override = AppTextOverride(color = StatusNeutral))
                AppText(text = walletNumber, style = AppTextStyle.BodyLarge, override = AppTextOverride(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun OutletInformationCard(outletInfo: OutletInfo, digitalPayment: DigitalPayment) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        AppText(text = "Outlet Information", style = AppTextStyle.TitleMedium)
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        AppIconLabelValue(
            label = "রেজিস্টার্ড ঠিকানা",
            value = outletInfo.address,
            icon = { AppIcon(icon = Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
            labelOverride = AppTextOverride(color = AccentIndigo),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "BMCC",
            value = outletInfo.bmccCode,
            icon = genericRowIcon,
            subValue = outletInfo.bmccName,
        )
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "Product Type",
            value = outletInfo.productType,
            icon = genericRowIcon,
            valueOverride = AppTextOverride(color = AccentIndigo, fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "Outlet location type", value = outletInfo.outletLocationType, icon = genericRowIcon)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "Outlet Type", value = outletInfo.outletType, icon = genericRowIcon)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "Card payment available?", value = digitalPayment.cardPaymentAvailable.toYesNo(), icon = genericRowIcon)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "Other MFS payment available?", value = digitalPayment.otherMfsAvailable.toYesNo(), icon = genericRowIcon)
    }
}

@Composable
private fun WalletInformationCard(walletInfo: WalletInfo) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        AppText(text = "Wallet Information", style = AppTextStyle.TitleMedium, override = AppTextOverride(color = AccentIndigo))
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        AppIconLabelValue(
            label = "Proposed Wallet Number",
            value = walletInfo.proposedWalletNumber,
            icon = genericRowIcon,
            trailingIcon = copyIcon,
        )
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "SIM Stays At Outlet?", value = walletInfo.simStaysAtOutlet.toBengaliYesNo(), icon = genericRowIcon)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "SIM Is Used In A Smartphone?", value = walletInfo.simUsedInSmartphone.toBengaliYesNo(), icon = genericRowIcon)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "SIM Is Owned By Shop Owner?", value = walletInfo.simOwnedByShopOwner.toBengaliYesNo(), icon = genericRowIcon)
    }
}

// The score bands (label, range, and color) are acquisition-scoring business data, not a generic
// design-system concept - kept here rather than baked into a designsystem component, built
// entirely from already-exposed pieces (AppText, AppIcon) plus Foundation layout, so there's no
// Material3-wrapping reason for it to live anywhere else. The active band comes straight from the
// API's own premiumness_band classification rather than being re-derived from score/ratio here.
private data class ScoreBand(val label: String, val range: String, val color: Color)

private val ScoreBands = listOf(
    ScoreBand("খুব ঝুঁকিপূর্ণ", "0-24", Color(0xFFE5BDB8)),
    ScoreBand("ঝুঁকিপূর্ণ", "25-49", Color(0xFFEDD1B6)),
    ScoreBand("মাঝারি", "50-74", Color(0xFFF0E3B8)),
    ScoreBand("ভালো", "75-89", Color(0xFF60AB9B)),
    ScoreBand("খুব ভালো", "90-100", Color(0xFFB4D7BF)),
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
        ScoreCircle(title = "প্রিমিয়ামনেস স্কোর", score = score, maxScore = MaxPremiumnessScore, ringColor = tierColor)
        ScoreBandIndicator(activeIndex = activeIndex, modifier = Modifier.fillMaxWidth())
        AppStepperButton(label = "বিস্তারিত দেখুন", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

// A one-off view for this screen only - no Material3-derived default color to justify a
// :designsystem home (unlike AppStatusBadge, this has exactly one caller and always receives an
// explicit color), so it's built directly here from plain Foundation border()/CircleShape. Only
// the ring is tinted by the active band - the score number itself stays plain/dark, matching the
// design (the color communicates the tier, the number doesn't need to repeat it).
@Composable
private fun ScoreCircle(title: String, score: Double, maxScore: Int, ringColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(120.dp)
            .border(border = BorderStroke(width = 6.dp, color = ringColor), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppText(text = title, style = AppTextStyle.Label, override = AppTextOverride(color = StatusNeutral))
            Row(verticalAlignment = Alignment.Bottom) {
                AppText(
                    text = "%.1f".format(score).toBengaliDigits(),
                    style = AppTextStyle.TitleLarge,
                    override = AppTextOverride(fontWeight = FontWeight.Bold),
                )
                AppText(
                    text = "/$maxScore".toBengaliDigits(),
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

// Single preview (no Dark variant) since ComposeFormAppTheme forces light theme regardless of
// system setting - that's how this screen actually renders in the real app, so a "Dark" tile
// here would show something the app never does. heightDp is tall enough to lay out the whole
// scrollable column (score section + 3 photos + all info cards) without clipping, since a
// default-height preview canvas would otherwise just show the top of the screen.
@Preview(name = "Acquisition Approval Detail", showBackground = true, heightDp = 2000)
@Composable
private fun AcquisitionApprovalDetailScreenPreview() {
    ComposeFormAppTheme {
        AcquisitionApprovalDetailScreen(onBack = {}, onApprove = {}, onReject = {})
    }
}

