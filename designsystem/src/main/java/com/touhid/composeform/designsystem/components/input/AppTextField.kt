package com.touhid.composeform.designsystem.components.input

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

enum class AppTextFieldType { Text, Number, Email, Password }

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    type: AppTextFieldType = AppTextFieldType.Text,
) {
    val keyboardType = when (type) {
        AppTextFieldType.Text -> KeyboardType.Text
        AppTextFieldType.Number -> KeyboardType.Number
        AppTextFieldType.Email -> KeyboardType.Email
        AppTextFieldType.Password -> KeyboardType.Password
    }
    val visualTransformation = if (type == AppTextFieldType.Password) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
    )
}
