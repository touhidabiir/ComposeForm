package com.touhid.composeform.designsystem.components.input

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.isSpecified
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.orElse

data class AppDropdownOption(val label: String, val labelOverride: AppTextOverride = AppTextOverride())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDropdown(
    options: List<AppDropdownOption>,
    selectedOption: AppDropdownOption?,
    onOptionSelected: (AppDropdownOption) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelOverride: AppTextOverride = AppTextOverride(),
) {
    var expanded by remember { mutableStateOf(false) }

    val baseTextStyle = LocalTextStyle.current
    val selectedOverride = selectedOption?.labelOverride ?: AppTextOverride()
    val resolvedTextStyle = baseTextStyle.copy(
        fontSize = selectedOverride.fontSize.orElse(baseTextStyle.fontSize),
        fontWeight = selectedOverride.fontWeight ?: baseTextStyle.fontWeight,
        color = if (selectedOverride.color.isSpecified) selectedOverride.color else baseTextStyle.color,
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = selectedOption?.label.orEmpty(),
            onValueChange = {},
            readOnly = true,
            textStyle = resolvedTextStyle,
            label = label?.let { { AppText(text = it, override = labelOverride) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { AppText(text = option.label, override = option.labelOverride) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
