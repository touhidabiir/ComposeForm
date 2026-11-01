package com.touhid.composeform.feature.login.ui.otp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppLinearProgressIndicator
import com.touhid.composeform.designsystem.components.surface.AppProgressDialog
import com.touhid.composeform.designsystem.components.surface.AppSnackbarHost
import com.touhid.composeform.designsystem.components.surface.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.feature.login.ui.BuildConfig
import com.touhid.composeform.feature.login.ui.LoginConfig
import com.touhid.composeform.feature.login.ui.LoginTitle
import com.touhid.composeform.feature.login.ui.MutedTextColor
import com.touhid.composeform.feature.login.ui.R
import com.touhid.composeform.feature.login.ui.VersionLabel
import com.touhid.composeform.session.domain.BangladeshPhoneNumber

// Release builds fill the OTP from the SMS only; debug builds also let it be typed, since the
// mocked backend sends no SMS.
private val ManualOtpEntry = BuildConfig.DEBUG

@Composable
internal fun OtpScreen(
    onSignedIn: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val snackbar = rememberAppSnackbarHostState()
    val resentMessage = stringResource(R.string.login_otp_resent)

    SmsOtpRetriever(
        session = state.smsSession,
        otpLength = LoginConfig.OTP_LENGTH,
        onOtpReceived = viewModel::onOtpChange,
    )

    // Every box filled: nothing left to type, so the keyboard goes and the button is in reach.
    LaunchedEffect(state.isOtpComplete) {
        if (state.isOtpComplete) {
            keyboard?.hide()
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(state.signedIn) {
        if (state.signedIn) onSignedIn()
    }
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbar.showMessage(
            when (message) {
                is OtpMessage.Error -> message.text
                OtpMessage.Resent -> resentMessage
            },
        )
        viewModel.onMessageShown()
    }

    AppScaffold(snackbarHost = { AppSnackbarHost(snackbar) }) {
        OtpContent(
            state = state,
            manualEntry = ManualOtpEntry,
            onOtpChange = viewModel::onOtpChange,
            onResend = viewModel::onResend,
            onVerify = viewModel::onVerify,
        )
        if (state.isVerifying || state.isResending) AppProgressDialog()
    }
}

@Composable
private fun OtpContent(
    state: OtpState,
    manualEntry: Boolean,
    onOtpChange: (String) -> Unit,
    onResend: () -> Unit,
    onVerify: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VersionLabel(modifier = Modifier.padding(top = AppSpacing.Medium))
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        Image(
            painter = painterResource(R.drawable.login_illustration_otp),
            contentDescription = null,
            modifier = Modifier.size(width = 200.dp, height = 180.dp),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        LoginTitle(text = stringResource(R.string.login_otp_title))
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        AppLinearProgressIndicator(
            progress = { state.waitProgress },
            modifier = Modifier.width(120.dp).height(4.dp),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        AppText(
            text = BangladeshPhoneNumber.toInternational(state.phoneNumber),
            style = AppTextStyle.TitleMedium,
            override = AppTextOverride(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        AppText(
            text = stringResource(R.string.login_otp_waiting),
            style = AppTextStyle.BodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        OtpInput(
            value = state.otp,
            length = LoginConfig.OTP_LENGTH,
            onValueChange = onOtpChange,
            editable = manualEntry,
            contentDescription = stringResource(R.string.login_otp_input),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        ResendPrompt(enabled = state.canResend && !state.isResending, onResend = onResend)
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        SupportHotline()
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        AppButton(
            text = stringResource(R.string.login_sign_in),
            onClick = onVerify,
            enabled = state.canVerify,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
    }
}

// "Didn't get the OTP? Send again": faded until the wait is over.
@Composable
private fun ResendPrompt(enabled: Boolean, onResend: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onResend)
            .padding(AppSpacing.Small),
    ) {
        AppText(
            text = stringResource(R.string.login_otp_not_received) + " ",
            style = AppTextStyle.BodyLarge,
            color = if (enabled) MutedTextColor else MutedTextColor.copy(alpha = 0.45f),
        )
        AppText(
            text = stringResource(R.string.login_otp_resend),
            style = AppTextStyle.BodyLarge,
            color = if (enabled) BrandPrimary else BrandPrimary.copy(alpha = 0.45f),
            override = AppTextOverride(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun SupportHotline() {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(BrandPrimary.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                icon = Icons.Outlined.HeadsetMic,
                contentDescription = null,
                tint = BrandPrimary.copy(alpha = 0.6f),
            )
        }
        Spacer(modifier = Modifier.width(AppSpacing.Small))
        AppText(
            text = LoginConfig.SUPPORT_HOTLINE,
            style = AppTextStyle.BodyLarge,
            color = MutedTextColor,
            override = AppTextOverride(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.width(AppSpacing.Small))
        AppIconButton(
            icon = Icons.Filled.Call,
            contentDescription = stringResource(R.string.login_call_support),
            onClick = { context.dial(LoginConfig.SUPPORT_HOTLINE) },
            tint = BrandPrimary,
        )
    }
}

// Opens the dialer with the number filled in; needs no CALL_PHONE permission.
private fun Context.dial(number: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number.filterNot(Char::isWhitespace), null))
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // No dialer (e.g. a tablet without telephony): nothing to open.
    }
}

@Preview(name = "Light", showBackground = true, heightDp = 820)
@Preview(name = "Dark", showBackground = true, heightDp = 820, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OtpContentPreview() {
    ComposeFormTheme {
        OtpContent(
            state = OtpState(phoneNumber = "01937049061", otp = "123", waitProgress = 0.02f),
            manualEntry = true,
            onOtpChange = {},
            onResend = {},
            onVerify = {},
        )
    }
}
