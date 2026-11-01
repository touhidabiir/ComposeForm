package com.touhid.composeform.feature.login.ui.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.feature.login.ui.LoginConfig
import com.touhid.composeform.feature.login.ui.SmsAppHash
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.session.domain.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

sealed interface OtpMessage {
    data object Resent : OtpMessage
    data class Error(val text: String) : OtpMessage
}

data class OtpState(
    val phoneNumber: String,
    val otp: String = "",
    // 0..1: how much of LoginConfig.OTP_WAIT has passed since the code was sent.
    val waitProgress: Float = 0f,
    val canResend: Boolean = false,
    val isVerifying: Boolean = false,
    val isResending: Boolean = false,
    // Bumped on every resend, so the SMS listener restarts for the new code.
    val smsSession: Int = 0,
    // One-shot: cleared once the screen has shown it.
    val message: OtpMessage? = null,
    // Set once the OTP is verified and the session stored; the screen then ends sign-in.
    val signedIn: Boolean = false,
) {
    val isOtpComplete: Boolean get() = otp.length == LoginConfig.OTP_LENGTH
    val canVerify: Boolean get() = isOtpComplete && !isVerifying && !isResending && !signedIn
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val smsAppHash: SmsAppHash,
) : ViewModel() {

    private val phoneNumber: String = checkNotNull(savedStateHandle[ArgPhoneNumber]) {
        "OtpScreen requires the phone number the OTP was sent to"
    }

    private val _state = MutableStateFlow(OtpState(phoneNumber = phoneNumber))
    val state = _state.asStateFlow()

    private var countdown: Job? = null

    init {
        // The phone step has just sent the code.
        startCountdown()
    }

    // Typed (debug builds only) or read from the SMS.
    fun onOtpChange(input: String) {
        _state.update { it.copy(otp = input.filter(Char::isDigit).take(LoginConfig.OTP_LENGTH)) }
    }

    fun onVerify() {
        val current = _state.value
        if (!current.canVerify) return
        _state.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            when (val result = sessionRepository.verifyOtp(phoneNumber, current.otp)) {
                is AppResult.Success -> _state.update { it.copy(isVerifying = false, signedIn = true) }
                is AppResult.Failure -> _state.update {
                    it.copy(isVerifying = false, message = OtpMessage.Error(result.error.message))
                }
            }
        }
    }

    fun onResend() {
        val current = _state.value
        if (!current.canResend || current.isResending) return
        _state.update { it.copy(isResending = true) }
        viewModelScope.launch {
            when (val result = sessionRepository.requestOtp(phoneNumber, smsAppHash.value)) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            otp = "",
                            isResending = false,
                            canResend = false,
                            waitProgress = 0f,
                            smsSession = it.smsSession + 1,
                            message = OtpMessage.Resent,
                        )
                    }
                    startCountdown()
                }
                is AppResult.Failure -> _state.update {
                    it.copy(isResending = false, message = OtpMessage.Error(result.error.message))
                }
            }
        }
    }

    fun onMessageShown() {
        _state.update { it.copy(message = null) }
    }

    // Fills the bar under the title over LoginConfig.OTP_WAIT, then allows a resend.
    private fun startCountdown() {
        countdown?.cancel()
        countdown = viewModelScope.launch {
            val start = TimeSource.Monotonic.markNow()
            while (true) {
                val elapsed = start.elapsedNow()
                if (elapsed >= LoginConfig.OTP_WAIT) {
                    _state.update { it.copy(waitProgress = 1f, canResend = true) }
                    break
                }
                _state.update { it.copy(waitProgress = (elapsed / LoginConfig.OTP_WAIT).toFloat()) }
                delay(1.seconds)
            }
        }
    }

    companion object {
        // The OTP route's argument (LoginActivity), read here through SavedStateHandle.
        const val ArgPhoneNumber = "phoneNumber"
    }
}
