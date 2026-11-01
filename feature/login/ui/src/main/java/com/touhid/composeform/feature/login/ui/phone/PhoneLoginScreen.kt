package com.touhid.composeform.feature.login.ui.phone

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.common.BrandLogo
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.input.AppTextField
import com.touhid.composeform.designsystem.components.input.AppTextFieldType
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppProgressDialog
import com.touhid.composeform.designsystem.components.surface.AppSnackbarHost
import com.touhid.composeform.designsystem.components.surface.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.feature.login.ui.LoginConfig
import com.touhid.composeform.feature.login.ui.LoginTitle
import com.touhid.composeform.feature.login.ui.MutedTextColor
import com.touhid.composeform.feature.login.ui.R
import com.touhid.composeform.feature.login.ui.VersionLabel
import com.touhid.composeform.session.domain.BangladeshPhoneNumber

@Composable
internal fun PhoneLoginScreen(
    onOtpSent: (phoneNumber: String) -> Unit,
    viewModel: PhoneLoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current
    val snackbar = rememberAppSnackbarHostState()

    // A complete, valid number needs no more typing: drop the keyboard so the button is in reach.
    LaunchedEffect(state.isValid) {
        if (state.isValid) keyboard?.hide()
    }
    LaunchedEffect(state.otpSentTo) {
        state.otpSentTo?.let { phoneNumber ->
            viewModel.onOtpStepOpened()
            onOtpSent(phoneNumber)
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let { message ->
            snackbar.showMessage(message)
            viewModel.onErrorShown()
        }
    }

    AppScaffold(snackbarHost = { AppSnackbarHost(snackbar) }) {
        PhoneLoginContent(
            phoneNumber = state.phoneNumber,
            canSubmit = state.canSubmit,
            onPhoneNumberChange = viewModel::onPhoneNumberChange,
            onSubmit = viewModel::onSubmit,
        )
        if (state.isSubmitting) AppProgressDialog()
    }
}

@Composable
private fun PhoneLoginContent(
    phoneNumber: String,
    canSubmit: Boolean,
    onPhoneNumberChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = AppSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VersionLabel(modifier = Modifier.padding(top = AppSpacing.Medium))

        // Takes whatever height the form below leaves, so the keyboard shrinks the logo area
        // rather than pushing the field off screen.
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                BrandLogo()
                Image(
                    painter = painterResource(R.drawable.login_illustration_city),
                    contentDescription = null,
                    modifier = Modifier.width(260.dp),
                )
            }
        }

        LoginTitle(text = stringResource(R.string.login_sign_in))
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        Box(modifier = Modifier.size(width = 24.dp, height = 3.dp).background(BrandPrimary))
        Spacer(modifier = Modifier.height(AppSpacing.Large))

        AppTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.login_phone_label),
            type = AppTextFieldType.Phone,
            maxLength = BangladeshPhoneNumber.LENGTH,
        )
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        AppButton(
            text = stringResource(R.string.login_sign_in),
            onClick = onSubmit,
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(AppSpacing.Large))
        PrivacyPolicyNotice()
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
    }
}

@Composable
private fun PrivacyPolicyNotice() {
    val prefix = stringResource(R.string.login_privacy_prefix)
    val policy = stringResource(R.string.login_privacy_policy)
    val text = buildAnnotatedString {
        append(prefix)
        append("  ")
        withLink(
            LinkAnnotation.Url(
                url = LoginConfig.PRIVACY_POLICY_URL,
                styles = TextLinkStyles(style = SpanStyle(color = BrandPrimary, fontWeight = FontWeight.Medium)),
            ),
        ) {
            append(policy)
        }
    }
    AppText(text = text, style = AppTextStyle.BodyMedium, color = MutedTextColor, textAlign = TextAlign.Center)
}

@Preview(name = "Light", showBackground = true, heightDp = 780)
@Preview(name = "Dark", showBackground = true, heightDp = 780, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PhoneLoginContentPreview() {
    ComposeFormTheme {
        PhoneLoginContent(phoneNumber = "01937049061", canSubmit = true, onPhoneNumberChange = {}, onSubmit = {})
    }
}
