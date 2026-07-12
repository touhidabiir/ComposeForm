package com.touhid.composeform.designsystem.components.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.touhid.composeform.designsystem.components.icon.AppIconButton

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
    readOnly: Boolean = false,
    type: AppTextFieldType = AppTextFieldType.Text,
    onTrailingActionClick: (() -> Unit)? = null,
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

    val wholeFieldOpensPicker = readOnly && enabled && onTrailingActionClick != null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (wholeFieldOpensPicker) {
            modifier.clickable(onClick = onTrailingActionClick!!)
        } else {
            modifier
        },
        enabled = if (wholeFieldOpensPicker) false else enabled,
        readOnly = readOnly,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = onTrailingActionClick?.let { onClick ->
            { AppIconButton(icon = Icons.Filled.Search, contentDescription = "Open picker", onClick = onClick) }
        },
        colors = if (wholeFieldOpensPicker) {
            OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            OutlinedTextFieldDefaults.colors()
        },
    )
}
