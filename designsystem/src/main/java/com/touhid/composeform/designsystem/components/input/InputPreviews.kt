package com.touhid.composeform.designsystem.components.input

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InputPreview() {
    ComposeFormTheme {
        Surface {
            Column(
                modifier = Modifier.padding(AppSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                AppSearchField(value = "", onValueChange = {}, placeholder = "No icons at all")
                AppSearchField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Search...",
                    leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
                )
                AppSearchField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Search by location...",
                    leadingIcon = { Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null) },
                )
                AppSearchField(
                    value = "Bakalia",
                    onValueChange = {},
                    trailingIcon = { Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear") },
                )
                AppTextField(
                    value = "Jane Doe",
                    onValueChange = {},
                    label = "Name",
                )
                AppTextField(
                    value = "",
                    onValueChange = {},
                    label = "Email",
                    isError = true,
                    supportingText = "Required",
                )
                AppCheckbox(checked = true, onCheckedChange = {}, label = "Subscribe to updates")
                AppCheckbox(
                    checked = true,
                    onCheckedChange = {},
                    label = "Checkbox trailing the label",
                    position = AppCheckboxPosition.End,
                    modifier = Modifier.fillMaxWidth(),
                )
                AppRadioButton(selected = true, onClick = {}, label = "Option A")
                AppRadioButton(selected = false, onClick = {}, label = "Option B")
                AppRadioCheckCircle(selected = true, onClick = {}, label = "Option A")
                AppRadioCheckCircle(selected = false, onClick = {}, label = "Option B")
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
                    AppRadioToggleChip(
                        selected = true,
                        onClick = {},
                        label = "Yes",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
                    )
                    AppRadioToggleChip(
                        selected = false,
                        onClick = {},
                        label = "No",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
                    )
                }
                AppSwitch(checked = true, onCheckedChange = {}, label = "Enable notifications")
                AppDropdown(
                    options = listOf(
                        AppDropdownOption("One"),
                        AppDropdownOption("Two"),
                        AppDropdownOption("Three"),
                    ),
                    selectedOption = AppDropdownOption("One"),
                    onOptionSelected = {},
                    label = "Choose an option",
                )
            }
        }
    }
}
