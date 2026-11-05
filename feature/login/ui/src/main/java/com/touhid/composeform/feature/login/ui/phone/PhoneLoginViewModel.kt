package com.touhid.composeform.feature.login.ui.phone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.feature.login.ui.SmsAppHash
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.feature.login.domain.BangladeshPhoneNumber
import com.touhid.composeform.feature.login.domain.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhoneLoginState(
    val phoneNumber: String = "",
    val isSubmitting: Boolean = false,
    // One-shot: cleared once the screen has shown it.
    val error: String? = null,
    // One-shot: the number an OTP was just sent to. The screen moves to the OTP step and clears it.
    val otpSentTo: String? = null,
) {
    val isValid: Boolean get() = BangladeshPhoneNumber.isValid(phoneNumber)
    val canSubmit: Boolean get() = isValid && !isSubmitting
}

@HiltViewModel
class PhoneLoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val smsAppHash: SmsAppHash,
) : ViewModel() {

    private val _state = MutableStateFlow(PhoneLoginState())
    val state = _state.asStateFlow()

    // Digits only, never more than a full number: the phone keyboard also offers + * # and the
    // like, and a pasted "+880..." would otherwise never become valid.
    fun onPhoneNumberChange(input: String) {
        _state.update { it.copy(phoneNumber = input.filter(Char::isDigit).take(BangladeshPhoneNumber.LENGTH)) }
    }

    fun onSubmit() {
        val current = _state.value
        if (!current.canSubmit) return
        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val phoneNumber = current.phoneNumber
            when (val result = loginRepository.requestOtp(phoneNumber, smsAppHash.value)) {
                is AppResult.Success -> _state.update { it.copy(isSubmitting = false, otpSentTo = phoneNumber) }
                is AppResult.Failure -> _state.update { it.copy(isSubmitting = false, error = result.error.message) }
            }
        }
    }

    fun onOtpStepOpened() {
        _state.update { it.copy(otpSentTo = null) }
    }

    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }
}
