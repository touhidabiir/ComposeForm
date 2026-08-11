package com.touhid.composeform.acquisition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.common.copyIconButton
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppButtonStyle
import com.touhid.composeform.designsystem.components.button.AppOutlinedButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.input.AppCheckbox
import com.touhid.composeform.designsystem.components.input.AppTextField
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppBottomActionBar
import com.touhid.composeform.designsystem.components.surface.AppBottomSheet
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppHorizontalDivider
import com.touhid.composeform.designsystem.components.surface.AppProgressDialog
import com.touhid.composeform.designsystem.components.surface.AppProgressIndicator
import com.touhid.composeform.designsystem.components.surface.AppSnackbarHost
import com.touhid.composeform.designsystem.components.surface.AppSnackbarResult
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.text.AppIconLabelValue
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusNeutral
import com.touhid.composeform.designsystem.theme.StatusNeutralContainer
import com.touhid.composeform.designsystem.theme.StatusSuccess
import com.touhid.composeform.designsystem.theme.StatusWarning
import com.touhid.composeform.network.model.AcquisitionAudit
import com.touhid.composeform.network.model.AcquisitionDetail
import com.touhid.composeform.network.model.AcquisitionImages
import com.touhid.composeform.network.model.AcquisitionReason
import com.touhid.composeform.network.model.ContactInfo
import com.touhid.composeform.network.model.ContactPerson
import com.touhid.composeform.network.model.DigitalPayment
import com.touhid.composeform.network.model.Facility
import com.touhid.composeform.network.model.LeadCloser
import com.touhid.composeform.network.model.OutletInfo
import com.touhid.composeform.network.model.PremiumnessScoreRange
import com.touhid.composeform.network.model.SurveyResponse
import com.touhid.composeform.network.model.WalletInfo

private val RowIconSize = 16.dp
private const val MaxPremiumnessScore = 100

// Same brand secondary accent as the list screens - one caller here too, kept local rather than
// promoted into :designsystem's theme (see LeadDashboardScreen.kt for the fuller rationale).
private val AccentIndigo = Color(0xFF675C92)

private val BengaliDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
private fun String.toBengaliDigits(): String = map { c -> if (c in '0'..'9') BengaliDigits[c - '0'] else c }.joinToString("")

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
    viewModel: AcquisitionApprovalDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AcquisitionApprovalDetailContent(
        state = state,
        onBack = onBack,
        onApprove = onApprove,
        onReject = onReject,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun AcquisitionApprovalDetailContent(
    state: AcquisitionApprovalDetailState,
    onBack: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onAction: (AcquisitionApprovalDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = rememberAppSnackbarHostState()
    LaunchedEffect(state.error) {
        if (state.error == null) return@LaunchedEffect
        val result = snackbarHostState.showMessage(message = "Please try again", actionLabel = "Retry")
        if (result == AppSnackbarResult.ActionPerformed) onAction(AcquisitionApprovalDetailAction.OnRetry)
    }

    // Modal only when there's already content behind it (a retry) - the initial load has
    // nothing to protect yet, and blocking Back access while a slow/hung request is in flight
    // would trap the user on this screen with no way out.
    if (state.isLoading && state.detail != null) {
        AppProgressDialog()
    }

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
            if (state.detail != null) {
                AppBottomActionBar(cornerRadius = AppSpacing.Medium) {
                    // onApprove/onReject (navigation-worthy, currently just pop the back stack -
                    // see MainActivity) are intentionally not called here. Tapping either button
                    // now opens its reason sheet instead; onApprove/onReject stay reserved for a
                    // future task once a real submit call exists to navigate away on success.
                    AppOutlinedButton(
                        text = "Reject",
                        onClick = { onAction(AcquisitionApprovalDetailAction.OnRejectTapped) },
                        buttonType = AppButtonStyle.Danger,
                        leadingIcon = { AppIcon(icon = Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(RowIconSize), tint = StatusError) },
                        modifier = Modifier.weight(1f),
                    )
                    AppButton(
                        text = "Approve",
                        onClick = { onAction(AcquisitionApprovalDetailAction.OnApproveTapped) },
                        buttonType = AppButtonStyle.Success,
                        leadingIcon = { AppIcon(icon = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(RowIconSize), tint = Color.White) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
    ) {
        if (state.detail != null) {
            AcquisitionDetailBody(detail = state.detail)
        } else if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AppProgressIndicator()
            }
        } else {
            AppText(
                text = "কোনো তথ্য পাওয়া যায়নি",
                modifier = Modifier.fillMaxWidth().padding(AppSpacing.Medium),
                textAlign = TextAlign.Center,
            )
        }
    }

    state.openReasonSheet?.let { sheetType ->
        ApprovalReasonSheet(
            type = sheetType,
            reasons = state.reasons,
            reasonsLoading = state.reasonsLoading,
            reasonsError = state.reasonsError,
            onDismissRequest = { onAction(AcquisitionApprovalDetailAction.OnReasonSheetDismissed) },
            onConfirm = { reasonIds, note ->
                onAction(
                    if (sheetType == ReasonSheetType.Approve) {
                        AcquisitionApprovalDetailAction.OnApproveConfirmed(reasonIds, note)
                    } else {
                        AcquisitionApprovalDetailAction.OnRejectConfirmed(reasonIds, note)
                    },
                )
            },
        )
    }
}

@Composable
private fun AcquisitionDetailBody(detail: AcquisitionDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        ShopIdentityCard(shopName = detail.shopName, walletNumber = detail.walletNumber)

        AppCard(modifier = Modifier.fillMaxWidth()) {
            ScoreSection(score = detail.premiumnessScore, ranges = detail.premiumnessScoreRanges, surveyResponses = detail.surveyResponses)
        }

        PhotoBlock(caption = "আউটলেটের বাহিরের ছবি", counter = "1/3".toBengaliDigits(), imageUrl = detail.images.shopImageOutside)
        PhotoBlock(caption = "আউটলেটের ভিতরের ছবি", counter = "2/3".toBengaliDigits(), imageUrl = detail.images.shopImageInside)
        PhotoBlock(caption = "ব্যবসার পরিচয়পত্রের ছবি", counter = "3/3".toBengaliDigits(), imageUrl = detail.images.businessProofImage)

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
                trailingIcon = copyIconButton(detail.contactInfo.outletOwner.phoneNumber),
            )
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            AppIconLabelValue(
                label = "Shop Operator Info (if different)",
                value = detail.contactInfo.contactPerson.name,
                icon = { AppIcon(icon = Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(RowIconSize)) },
            )
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            AppIconLabelValue(
                value = detail.contactInfo.contactPerson.phoneNumber,
                trailingIcon = copyIconButton(detail.contactInfo.contactPerson.phoneNumber),
            )
        }

        OutletInformationCard(outletInfo = detail.outletInfo, digitalPayment = detail.digitalPayment)
        WalletInformationCard(walletInfo = detail.walletInfo)
    }
}

@Composable
private fun ShopIdentityCard(shopName: String, walletNumber: String) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium)) {
            AppIcon(icon = Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.size(24.dp), tint = BrandPrimary)
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
            trailingIcon = copyIconButton(walletInfo.proposedWalletNumber),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "SIM Stays At Outlet?", value = walletInfo.simStaysAtOutlet.toBengaliYesNo(), icon = genericRowIcon)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "SIM Is Used In A Smartphone?", value = walletInfo.simUsedInSmartphone.toBengaliYesNo(), icon = genericRowIcon)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(label = "SIM Is Owned By Shop Owner?", value = walletInfo.simOwnedByShopOwner.toBengaliYesNo(), icon = genericRowIcon)
    }
}

// A hex color string (e.g. "#B4D7BF") from the API into a Compose Color - premiumnessScoreRanges
// drives both the band boundaries and which one is active from the backend now, so the client no
// longer hardcodes a band table; this is just the one piece (parsing a color string) Gson can't
// do on its own.
private fun String.toComposeColor(): Color = Color(android.graphics.Color.parseColor(this))

private val PhotoPlaceholderColor = Color(0xFFCFD8DC)

// Falls back to when the API sends an empty premiumness_score_ranges list - unreachable via
// today's mock (MockJson.ACQUISITION_DETAIL always supplies 5 populated ranges), but ranges[activeIndex]
// would otherwise crash the whole screen on a genuinely empty response instead of just rendering
// an un-tinted ring.
private val UnknownScoreTierColor = Color(0xFFCFD8DC)

@Composable
private fun ScoreSection(score: Double, ranges: List<PremiumnessScoreRange>, surveyResponses: List<SurveyResponse>) {
    val activeIndex = ranges.indexOfFirst { it.isActive }.takeIf { it >= 0 } ?: (ranges.size / 2)
    val tierColor = ranges.getOrNull(activeIndex)?.color?.toComposeColor() ?: UnknownScoreTierColor
    var showSurveyResponses by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
    ) {
        ScoreCircle(title = "প্রিমিয়ামনেস স্কোর", score = score, maxScore = MaxPremiumnessScore, ringColor = tierColor)
        ScoreBandIndicator(ranges = ranges, activeIndex = activeIndex, modifier = Modifier.fillMaxWidth())
        AppStepperButton(label = "বিস্তারিত দেখুন", onClick = { showSurveyResponses = true }, modifier = Modifier.fillMaxWidth())
    }

    if (showSurveyResponses) {
        SurveyResponsesSheet(responses = surveyResponses, onDismissRequest = { showSurveyResponses = false })
    }
}

// The Q&A breakdown behind the premiumness score, opened from ScoreSection's "বিস্তারিত দেখুন"
// button - one caller, built from AppBottomSheet the same way LeadDashboardScreen's
// RejectionDetailsSheet is.
@Composable
private fun SurveyResponsesSheet(responses: List<SurveyResponse>, onDismissRequest: () -> Unit) {
    AppBottomSheet(onDismissRequest = onDismissRequest, expandedByDefault = true) {
        SurveyResponsesSheetContent(responses = responses, onDismissRequest = onDismissRequest)
    }
}

// Split out from SurveyResponsesSheet so it can be previewed directly - ModalBottomSheet (which
// AppBottomSheet wraps) renders its content through a Popup on its own window, which Compose's
// static preview renderer never draws into, so a preview built around AppBottomSheet itself would
// always show a blank canvas.
@Composable
private fun ColumnScope.SurveyResponsesSheetContent(responses: List<SurveyResponse>, onDismissRequest: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BrandPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(icon = Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.width(AppSpacing.Small))
        AppText(
            text = "প্রশ্ন অনুযায়ী উত্তর",
            style = AppTextStyle.TitleMedium,
            override = AppTextOverride(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        AppIconButton(icon = Icons.Filled.Close, contentDescription = "Close", onClick = onDismissRequest)
    }
    Spacer(modifier = Modifier.height(AppSpacing.Medium))
    AppHorizontalDivider()
    responses.forEachIndexed { index, response ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.Medium),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AppText(text = response.question, style = AppTextStyle.BodyMedium, modifier = Modifier.weight(1f))
            AppText(
                text = "উত্তর: ${response.answer}",
                style = AppTextStyle.BodyMedium,
                override = AppTextOverride(color = BrandPrimary, fontWeight = FontWeight.Bold),
            )
        }
        if (index != responses.lastIndex) {
            AppHorizontalDivider()
        }
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
            .size(160.dp)
            .border(border = BorderStroke(width = 16.dp, color = ringColor), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.Small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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

private val ScoreBandBarHeight = 16.dp

@Composable
private fun ScoreBandIndicator(ranges: List<PremiumnessScoreRange>, activeIndex: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.ExtraSmall)) {
        ranges.forEachIndexed { index, range ->
            val isActive = index == activeIndex
            val color = range.color.toComposeColor()
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isActive) {
                    AppIcon(
                        icon = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(RowIconSize),
                    )
                } else {
                    Spacer(modifier = Modifier.size(RowIconSize))
                }
                // A fixed-height box with the bar bottom-aligned inside it, so the active band's
                // taller bar grows upward instead of pushing the label below it further down -
                // every column's bar bottom (and the label beneath it) lines up at the same y.
                Box(
                    modifier = Modifier.fillMaxWidth().height(ScoreBandBarHeight),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isActive) ScoreBandBarHeight else 8.dp)
                            .background(color = color, shape = RoundedCornerShape(percent = 50)),
                    )
                }
                Spacer(modifier = Modifier.height(AppSpacing.ExtraSmall))
                AppText(
                    text = "${range.minScore}-${range.maxScore}".toBengaliDigits(),
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
private fun PhotoBlock(caption: String, counter: String, imageUrl: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = caption,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(AppSpacing.Small)),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(PhotoPlaceholderColor),
            error = ColorPainter(PhotoPlaceholderColor),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppText(text = caption, style = AppTextStyle.BodyMedium)
            AppText(text = counter, style = AppTextStyle.Label)
        }
    }
}

private val LikertLabels = listOf("তীব্র দ্বিমত", "দ্বিমত", "নিরপেক্ষ", "একমত", "তীব্র একমত")
private val LikertCircleSize = 24.dp
private val LikertConnectorHeight = 2.dp
private val ReasonCardCheckIconSize = 14.dp
private val ReasonDotSize = 8.dp

private data class ReasonSheetCopy(
    val reasonHeading: String,
    val reasonSubtitle: String,
    val accentColor: Color,
    val confirmLabel: String,
    val confirmButtonStyle: AppButtonStyle,
)

private fun reasonSheetCopy(type: ReasonSheetType): ReasonSheetCopy = when (type) {
    ReasonSheetType.Approve -> ReasonSheetCopy(
        reasonHeading = "অনুমোদনের কারণ",
        reasonSubtitle = "লিড অনুমোদনের কারণ নির্বাচন করুন।",
        accentColor = StatusSuccess,
        confirmLabel = "Approve Lead",
        confirmButtonStyle = AppButtonStyle.Success,
    )
    ReasonSheetType.Reject -> ReasonSheetCopy(
        reasonHeading = "প্রত্যাখ্যানের কারণ",
        reasonSubtitle = "লিড প্রত্যাখ্যানের কারণ নির্বাচন করুন।",
        // Reuses BrandPrimary (already this pink/magenta elsewhere in this file, e.g.
        // ShopIdentityCard's Storefront icon) rather than StatusError, since the design's reject
        // accent (card border/checkbox) is a distinct pink from the confirm button's solid red.
        accentColor = BrandPrimary,
        confirmLabel = "Reject Lead",
        confirmButtonStyle = AppButtonStyle.Danger,
    )
}

// The Approve/Reject reason-picker sheet opened from the bottom bar - a thin AppBottomSheet
// wrapper plus a separately-previewable ColumnScope content function, same split as
// SurveyResponsesSheet/SurveyResponsesSheetContent above (ModalBottomSheet renders via a Popup the
// static preview renderer can't capture).
@Composable
private fun ApprovalReasonSheet(
    type: ReasonSheetType,
    reasons: List<AcquisitionReason>,
    reasonsLoading: Boolean,
    reasonsError: String?,
    onDismissRequest: () -> Unit,
    onConfirm: (reasonIds: List<Int>, note: String) -> Unit,
) {
    AppBottomSheet(onDismissRequest = onDismissRequest, expandedByDefault = true) {
        ApprovalReasonSheetContent(
            type = type,
            reasons = reasons,
            reasonsLoading = reasonsLoading,
            reasonsError = reasonsError,
            onConfirm = onConfirm,
        )
    }
}

@Composable
private fun ColumnScope.ApprovalReasonSheetContent(
    type: ReasonSheetType,
    reasons: List<AcquisitionReason>,
    reasonsLoading: Boolean,
    reasonsError: String?,
    onConfirm: (reasonIds: List<Int>, note: String) -> Unit,
) {
    val copy = reasonSheetCopy(type)
    var likertIndex by remember(type) { mutableStateOf(2) }
    var selectedReasonIds by remember(type) { mutableStateOf(setOf<Int>()) }
    var noteText by remember(type) { mutableStateOf("") }

    AppText(text = "যাচাইকরণ", style = AppTextStyle.TitleMedium, override = AppTextOverride(fontWeight = FontWeight.Bold))
    Spacer(modifier = Modifier.height(AppSpacing.ExtraSmall))
    AppText(
        text = "আপনি কি এই প্রিমিয়ামনেস স্কোরের সাথে একমত?",
        style = AppTextStyle.BodyMedium,
        override = AppTextOverride(color = StatusNeutral),
    )
    Spacer(modifier = Modifier.height(AppSpacing.Medium))
    LikertScaleIndicator(selectedIndex = likertIndex, onSelect = { likertIndex = it }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(AppSpacing.Medium))

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
        Box(modifier = Modifier.size(ReasonDotSize).background(color = copy.accentColor, shape = CircleShape))
        AppText(text = copy.reasonHeading, style = AppTextStyle.TitleMedium)
    }
    Spacer(modifier = Modifier.height(AppSpacing.ExtraSmall))
    AppText(text = copy.reasonSubtitle, style = AppTextStyle.BodyMedium, override = AppTextOverride(color = StatusNeutral))
    Spacer(modifier = Modifier.height(AppSpacing.Medium))

    when {
        reasonsLoading -> Box(modifier = Modifier.fillMaxWidth().padding(AppSpacing.Medium), contentAlignment = Alignment.Center) {
            AppProgressIndicator()
        }
        reasonsError != null -> AppText(text = reasonsError, style = AppTextStyle.BodyMedium, override = AppTextOverride(color = StatusError))
        else -> Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
            reasons.forEach { reason ->
                val isSelected = reason.id in selectedReasonIds
                ReasonCheckboxCard(
                    reason = reason,
                    selected = isSelected,
                    accentColor = copy.accentColor,
                    onToggle = {
                        selectedReasonIds = if (isSelected) selectedReasonIds - reason.id else selectedReasonIds + reason.id
                    },
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(AppSpacing.Medium))

    AppTextField(
        value = noteText,
        onValueChange = { noteText = it },
        placeholder = "এখানে কারণ উল্লেখ করুন...",
        singleLine = false,
        modifier = Modifier.fillMaxWidth().height(96.dp),
    )
    Spacer(modifier = Modifier.height(AppSpacing.Medium))

    AppButton(
        text = copy.confirmLabel,
        onClick = { onConfirm(selectedReasonIds.toList(), noteText) },
        buttonType = copy.confirmButtonStyle,
        enabled = selectedReasonIds.isNotEmpty(),
        modifier = Modifier.fillMaxWidth(),
    )
}

// A single selectable reason row - bordered card wrapping AppCheckbox, whose border and checkbox
// fill both pick up the sheet's accent color when selected (a plain gray border otherwise). Not a
// :designsystem primitive - one caller here, no Material3-derived default it relies on.
@Composable
private fun ReasonCheckboxCard(reason: AcquisitionReason, selected: Boolean, accentColor: Color, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(width = if (selected) 1.5.dp else 1.dp, color = if (selected) accentColor else StatusNeutralContainer),
                shape = RoundedCornerShape(AppSpacing.Small),
            )
            .padding(horizontal = AppSpacing.Small, vertical = AppSpacing.ExtraSmall),
    ) {
        AppCheckbox(checked = selected, onCheckedChange = { onToggle() }, label = reason.reason, checkedColor = accentColor)
    }
}

// The "do you agree with the score" agreement scale - a tappable, fixed 5-label stepper, visually
// similar to ScoreBandIndicator above but NOT sharing a composable with it: ScoreBandIndicator is
// read-only and API-driven (variable band count/colors), this is fixed-label, single fixed accent,
// and tappable. Unifying them would need three independent branches (fixed-vs-dynamic labels,
// tappable-vs-read-only, circle-vs-bar rendering) for what's really just two unrelated callers -
// kept separate rather than forcing an awkward shared abstraction.
@Composable
private fun LikertScaleIndicator(selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        LikertLabels.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = { onSelect(index) }),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    LikertConnector(visible = index != 0)
                    LikertStepCircle(selected = isSelected)
                    LikertConnector(visible = index != LikertLabels.lastIndex)
                }
                Spacer(modifier = Modifier.height(AppSpacing.ExtraSmall))
                AppText(
                    text = label,
                    style = AppTextStyle.Label,
                    textAlign = TextAlign.Center,
                    override = if (isSelected) AppTextOverride(fontWeight = FontWeight.Bold) else AppTextOverride(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// Half of the connecting line on one side of a step's circle - invisible on the outer edges
// (step 0's leading half, the last step's trailing half) so the visible halves read as one
// continuous line running through every circle's center.
@Composable
private fun RowScope.LikertConnector(visible: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(LikertConnectorHeight)
            .background(if (visible) StatusNeutralContainer else Color.Transparent),
    )
}

@Composable
private fun LikertStepCircle(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(LikertCircleSize)
            .background(color = if (selected) StatusWarning else Color.White, shape = CircleShape)
            .border(width = 2.dp, color = if (selected) StatusWarning else StatusNeutralContainer, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            AppIcon(icon = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(ReasonCardCheckIconSize), tint = Color.White)
        }
    }
}

private val PreviewApproveReasons = listOf(
    AcquisitionReason(id = 3, reason = "লেনদেন নিয়মিত করে"),
    AcquisitionReason(id = 4, reason = "ব্যবসা ভালো চলছে"),
    AcquisitionReason(id = 5, reason = "দোকান সবসময় খোলা থাকে"),
    AcquisitionReason(id = 6, reason = "আগের ঋণ ঠিকমতো শোধ করেছে"),
    AcquisitionReason(id = 7, reason = "পেমেন্ট নিতে আগ্রহী"),
)

private val PreviewRejectReasons = listOf(
    AcquisitionReason(id = 8, reason = "প্রয়োজনীয় কাগজপত্র অসম্পূর্ণ"),
    AcquisitionReason(id = 9, reason = "ঠিকানা যাচাই করা যায়নি"),
    AcquisitionReason(id = 10, reason = "প্রিমিয়ামনেস স্কোর প্রত্যাশিত মানের কম"),
    AcquisitionReason(id = 11, reason = "যোগাযোগের তথ্যে অসামঞ্জস্য"),
    AcquisitionReason(id = 12, reason = "একই ওয়ালেট নম্বর ইতিমধ্যে নিবন্ধিত"),
)

// Rendered directly inside a plain Column (mimicking AppBottomSheet's own white background/
// padding) rather than through the real sheet, for the same Popup/preview-renderer reason as
// SurveyResponsesSheetPreview below. heightDp is tall enough to show the full content (Likert
// scale + all reason rows + text field + confirm button) unclipped.
@Preview(name = "Approval Reason Sheet - Approve", showBackground = true, heightDp = 900)
@Composable
private fun ApprovalReasonSheetContentApprovePreview() {
    ComposeFormAppTheme {
        Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(AppSpacing.Medium)) {
            ApprovalReasonSheetContent(
                type = ReasonSheetType.Approve,
                reasons = PreviewApproveReasons,
                reasonsLoading = false,
                reasonsError = null,
                onConfirm = { _, _ -> },
            )
        }
    }
}

@Preview(name = "Approval Reason Sheet - Reject", showBackground = true, heightDp = 900)
@Composable
private fun ApprovalReasonSheetContentRejectPreview() {
    ComposeFormAppTheme {
        Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(AppSpacing.Medium)) {
            ApprovalReasonSheetContent(
                type = ReasonSheetType.Reject,
                reasons = PreviewRejectReasons,
                reasonsLoading = false,
                reasonsError = null,
                onConfirm = { _, _ -> },
            )
        }
    }
}

private val PreviewDetail = AcquisitionDetail(
    id = 100238471,
    displayId = "LEAD-2026-100238471",
    shopName = "Romij Electric",
    walletNumber = "01723456789",
    status = "submitted",
    premiumnessScore = 71.25,
    premiumnessScoreRanges = listOf(
        PremiumnessScoreRange(minScore = 0, maxScore = 12, isActive = false, color = "#E5BDB8"),
        PremiumnessScoreRange(minScore = 12, maxScore = 25, isActive = false, color = "#EDD1B6"),
        PremiumnessScoreRange(minScore = 25, maxScore = 37, isActive = false, color = "#F0E3B8"),
        PremiumnessScoreRange(minScore = 37, maxScore = 62, isActive = false, color = "#60AB9B"),
        PremiumnessScoreRange(minScore = 62, maxScore = 100, isActive = true, color = "#B4D7BF"),
    ),
    images = AcquisitionImages(
        shopImageOutside = "https://picsum.photos/id/1011/800/600",
        shopImageInside = "https://picsum.photos/id/1012/800/600",
        businessProofImage = "https://picsum.photos/id/1013/800/600",
    ),
    outletInfo = OutletInfo(
        address = "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        district = "Chattogram",
        thana = "Bakalia",
        marketName = "Avengers Tower",
        bmccCode = "5002",
        bmccName = "Hardware & Electronics",
        productType = "Merchant Plus Lite A",
        outletLocationType = "Roadside",
        outletType = "Semi-permanent",
    ),
    digitalPayment = DigitalPayment(
        cardPaymentAvailable = true,
        otherMfsAvailable = false,
        facilities = listOf(Facility(name = "Card payment", completed = true)),
    ),
    contactInfo = ContactInfo(
        contactPerson = ContactPerson(name = "Kalam Bashir", phoneNumber = "01723456789", designation = "Manager"),
        outletOwner = ContactPerson(name = "Raju Ahmed Shetu", phoneNumber = "01723456789"),
    ),
    walletInfo = WalletInfo(
        proposedWalletNumber = "01723456789",
        simStaysAtOutlet = true,
        simUsedInSmartphone = true,
        simOwnedByShopOwner = true,
    ),
    surveyResponses = listOf(
        SurveyResponse(question = "Provides printed bills?", answer = "No", points = 20.7),
        SurveyResponse(question = "Spot lights count?", answer = "6-10", points = 8.0),
        SurveyResponse(question = "Accepts card payments?", answer = "No", points = 0.0),
        SurveyResponse(question = "Tube lights count?", answer = "4-6", points = 5.1),
        SurveyResponse(question = "Entrance door type?", answer = "Glass", points = 12.6),
    ),
    audit = AcquisitionAudit(
        createdAt = "2026-07-15T10:30:00+06:00",
        submittedAt = "2026-07-15T10:35:00+06:00",
        submittedBy = LeadCloser(name = "Jamal Bhuiyan", employeeId = "A11002912", whitelistingNumber = "1930119876", servingMa = "1930198765"),
    ),
)

// Single preview (no Dark variant) since ComposeFormAppTheme forces light theme regardless of
// system setting - that's how this screen actually renders in the real app, so a "Dark" tile
// here would show something the app never does. heightDp is tall enough to lay out the whole
// scrollable column (score section + 3 photos + all info cards) without clipping, since a
// default-height preview canvas would otherwise just show the top of the screen.
@Preview(name = "Acquisition Approval Detail", showBackground = true, heightDp = 2000)
@Composable
private fun AcquisitionApprovalDetailScreenPreview() {
    ComposeFormAppTheme {
        AcquisitionApprovalDetailContent(
            state = AcquisitionApprovalDetailState(isLoading = false, detail = PreviewDetail),
            onBack = {},
            onApprove = {},
            onReject = {},
            onAction = {},
        )
    }
}

// Renders SurveyResponsesSheetContent directly inside a plain Column rather than through
// SurveyResponsesSheet/AppBottomSheet - ModalBottomSheet draws its content in a Popup on its own
// window, which Compose's static preview renderer never captures, so a preview wrapping the real
// sheet call would always render blank. This Column mimics the sheet's own white background/
// padding (see AppBottomSheet) closely enough for a layout preview. heightDp is tall enough that
// all five PreviewDetail.surveyResponses rows are visible.
@Preview(name = "Survey Responses Sheet", showBackground = true, heightDp = 700)
@Composable
private fun SurveyResponsesSheetPreview() {
    ComposeFormAppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(AppSpacing.Medium),
        ) {
            SurveyResponsesSheetContent(responses = PreviewDetail.surveyResponses, onDismissRequest = {})
        }
    }
}
