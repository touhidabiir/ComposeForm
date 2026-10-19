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
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.StatusNeutral

enum class AppTextFieldType { Text, Number, Email, Password }

// One-off tones for flatWhenUnfocused's muted look - close to but distinct from
// StatusNeutralContainer, specific to this field state rather than a general-purpose theme token.
private val FlatUnfocusedContainerColor = Color(0xFFFAFAFB)
private val FlatUnfocusedBorderColor = Color(0xFFECEEF0)

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
    trailingIcon: @Composable (onClick: () -> Unit) -> Unit = { onClick ->
        AppIconButton(icon = Icons.Filled.Search, contentDescription = "Open picker", onClick = onClick, enabled = enabled)
    },
    // Muted/filled look (light fill + subtle border, StatusNeutral text) while unfocused,
    // reverting to the normal outlined look on focus - false keeps every existing caller (e.g.
    // :formbuilder's inputBox fields) exactly as before.
    flatWhenUnfocused: Boolean = false,
    // Only takes effect when singleLine is false - matches OutlinedTextField's own implicit
    // default, so leaving this unset changes nothing.
    maxLines: Int = Int.MAX_VALUE,
    // null (default) means unlimited, same as today. When set, input beyond the limit is silently
    // truncated rather than shown as an error or counted.
    maxLength: Int? = null,
    // Applies only to color, for label/placeholder/supportingText - fontSize/fontWeight are
    // deliberately not threaded through, since Material3's OutlinedTextField manages the label's
    // own TextStyle internally to drive its floating/shrink animation, and forcing an explicit
    // style there risks breaking it. Color has no such interaction, so it's safe to override.
    textOverride: AppTextOverride = AppTextOverride(),
) {
    val overrideTextColor = if (textOverride.color.isSpecified) textOverride.color else Color.Unspecified
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

    val wholeFieldTriggersAction = readOnly && enabled && onTrailingActionClick != null

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(if (maxLength != null) newValue.take(maxLength) else newValue)
        },
        modifier = if (wholeFieldTriggersAction) {
            modifier.clickable(onClick = onTrailingActionClick!!)
        } else {
            modifier
        },
        enabled = if (wholeFieldTriggersAction) false else enabled,
        readOnly = readOnly,
        label = label?.let { { Text(it, color = overrideTextColor) } },
        placeholder = placeholder?.let { { Text(it, color = overrideTextColor) } },
        isError = isError,
        supportingText = supportingText?.let { { Text(it, color = overrideTextColor) } },
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = onTrailingActionClick?.let { onClick ->
            { trailingIcon(onClick) }
        },
        colors = when {
            wholeFieldTriggersAction -> {
                // Material3 resolves disabled colors before error colors, so forcing enabled=false
                // above would otherwise silently hide error styling on a required, read-only field.
                val textColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                val accentColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                val borderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                OutlinedTextFieldDefaults.colors(
                    disabledTextColor = textColor,
                    disabledContainerColor = Color.Transparent,
                    disabledBorderColor = borderColor,
                    disabledLabelColor = accentColor,
                    disabledTrailingIconColor = accentColor,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = accentColor,
                )
            }
            flatWhenUnfocused -> OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FlatUnfocusedContainerColor,
                unfocusedBorderColor = FlatUnfocusedBorderColor,
                unfocusedTextColor = StatusNeutral,
                unfocusedPlaceholderColor = StatusNeutral,
                unfocusedLabelColor = StatusNeutral,
                unfocusedTrailingIconColor = StatusNeutral,
                unfocusedSupportingTextColor = StatusNeutral,
            )
            else -> OutlinedTextFieldDefaults.colors()
        },
    )
}
