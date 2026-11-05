package com.touhid.composeform.feature.login.ui.otp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary

private val BoxBorderColor = Color(0xFF212121)

// One box per digit, drawn over a single hidden text field: typing, backspace and paste all work
// on one string, and the cursor always stays at the end. With editable = false the boxes only
// display the value (release builds, where the code comes from the SMS alone).
@Composable
internal fun OtpInput(
    value: String,
    length: Int,
    onValueChange: (String) -> Unit,
    editable: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = TextFieldValue(text = value, selection = TextRange(value.length)),
        onValueChange = { onValueChange(it.text.filter(Char::isDigit).take(length)) },
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .semantics { this.contentDescription = contentDescription },
        enabled = editable,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small + 2.dp)) {
                repeat(length) { index ->
                    // The box the next digit goes into (the last one once all are filled).
                    val isCurrent = focused && index == value.length.coerceAtMost(length - 1)
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) BrandPrimary else BoxBorderColor,
                                shape = RoundedCornerShape(6.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppText(text = value.getOrNull(index)?.toString().orEmpty(), style = AppTextStyle.TitleLarge)
                    }
                }
            }
        },
    )
}
