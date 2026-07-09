package com.touhid.composeform

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.formbuilder.FormFieldResult
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.JSON_FORM
import com.touhid.composeform.formbuilder.parseFormSchema
import com.touhid.composeform.formbuilder.schema.FormValue

private val SAMPLE_FORM_JSON = """
{
  "fields": [
    {
      "type": "text", "key": "heading", "label": "Sign Up",
      "style": { "size": 24, "weight": "bold" },
      "margin": { "top": 0, "bottom": 16, "left": 0, "right": 0 }
    },
    {
      "type": "inputBox", "key": "name", "label": "Name", "required": true, "inputType": "text",
      "style": { "size": 16, "weight": "medium" },
      "margin": { "top": 8, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "inputBox", "key": "email", "label": "Email", "required": true, "inputType": "email",
      "pattern": "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+${'$'}",
      "errorMessage": "Enter a valid email address",
      "margin": { "top": 8, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "inputBox", "key": "phone", "label": "Phone Number", "required": true, "inputType": "number",
      "pattern": "^[0-9]{10,15}${'$'}",
      "errorMessage": "Enter a valid phone number",
      "margin": { "top": 8, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "inputBox", "key": "password", "label": "Password", "required": true, "inputType": "password",
      "minLength": 8,
      "errorMessage": "Must be at least 8 characters",
      "margin": { "top": 8, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "radio", "key": "gender", "label": "Gender", "required": true, "orientation": "horizontal",
      "options": [
        { "id": "male", "value": "Male", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "female", "value": "Female", "style": { "color": "#D81B60" }, "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "others", "value": "Others", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } }
      ],
      "margin": { "top": 24, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "radio", "key": "newsletter", "label": "Subscribe to newsletter?", "required": true,
      "orientation": "horizontal", "appearance": "toggle",
      "options": [
        { "id": "yes", "value": "Yes", "default": true, "border": { "color": "#D81B60", "width": 2, "radius" : 24 }, "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 }, "padding": { "top": 12, "bottom": 12, "left": 16, "right": 16 } },
        { "id": "no", "value": "No", "border": { "color": "#D81B60", "width": 2, "radius" : 24 }, "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 }, "padding": { "top": 4, "bottom": 4, "left": 4, "right": 4 } }
      ],
      "margin": { "top": 24, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "radio", "key": "doorType", "label": "Door type", "required": true,
      "orientation": "vertical", "appearance": "check",
      "options": [
        { "id": "glass", "value": "Glass", "default": true, "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "wood", "value": "Wood", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "thaiGlass", "value": "Thai Glass", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "none", "value": "No door", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } }
      ],
      "margin": { "top": 24, "bottom": 8, "left": 0, "right": 0 },
      "padding": { "top": 8, "bottom": 8, "left": 8, "right": 8 },
      "border": { "color": "#9E9E9E", "width": 1, "radius": 12 }
    },
    {
      "type": "dropdown", "key": "country", "label": "Country", "required": true,
      "options": [
        { "id": "bangladesh", "value": "Bangladesh" },
        { "id": "india", "value": "India" },
        { "id": "pakistan", "value": "Pakistan" },
        { "id": "nepal", "value": "Nepal" }
      ],
      "margin": { "top": 8, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "checkboxGroup", "key": "interests", "label": "What do you like?", "required": false, "orientation": "vertical",
      "options": [
        { "id": "music", "value": "Music", "default": true, "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "books", "value": "Books", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "games", "value": "Games", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "gossiping", "value": "Gossiping", "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } },
        { "id": "coding", "value": "Coding", "style": { "weight": "bold" }, "margin": { "top": 4, "bottom": 4, "left": 4, "right": 4 } }
      ],
      "margin": { "top": 24, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "checkbox", "key": "acceptTerms", "label": "I agree to the terms and conditions", "required": true,
      "margin": { "top": 24, "bottom": 16, "left": 0, "right": 0 }
    },
    {
      "type": "submit", "key": "submit", "label": "Submit",
      "style": { "size": 18, "weight": "bold", "color": "#FFFFFF" },
      "margin": { "top": 16, "bottom": 16, "left": 16, "right": 16 }
    }
  ]
}
""".trimIndent()

private val PICKER_FORM_JSON = """
{
  "fields": [
    {
      "type": "text", "key": "heading", "label": "Pick a name",
      "style": { "size": 20, "weight": "bold" },
      "margin": { "bottom": 16 }
    },
    {
      "type": "inputBox", "key": "result", "label": "Name", "required": true, "inputType": "text",
      "margin": { "bottom": 8 }
    },
    {
      "type": "submit", "key": "submit", "label": "Confirm",
      "margin": { "top": 16 }
    }
  ]
}
""".trimIndent()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormTheme {
                val navController = rememberNavController()
                var activePickerKey by rememberSaveable { mutableStateOf<String?>(null) }
                var pendingResult by remember { mutableStateOf<FormFieldResult?>(null) }

                NavHost(navController = navController, startDestination = "form") {
                    composable("form") {
                        val schema = remember { parseFormSchema(JSON_FORM) }

                        LaunchedEffect(pendingResult) {
                            if (pendingResult != null) {
                                activePickerKey = null
                                pendingResult = null
                            }
                        }

                        AppScaffold(topBar = { scrollBehavior ->
                            AppTopBar(
                                title = "ComposeForm Demo",
                                scrollBehavior = scrollBehavior,
                                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                                onNavigationClick = { finish() },
                            )
                        }) {
                            FormRenderer(
                                schema = schema,
                                modifier = Modifier.padding(16.dp),
                                pendingResult = pendingResult,
                                onPickerFieldClick = { key ->
                                    activePickerKey = key
                                    navController.navigate("picker")
                                },
                                onSubmit = { values -> Log.d("FormDemo", values.toString()) },
                            )
                        }
                    }

                    composable("picker") {
                        val pickerSchema = remember { parseFormSchema(PICKER_FORM_JSON) }
                        AppScaffold(topBar = { scrollBehavior ->
                            AppTopBar(
                                title = "Select a value",
                                scrollBehavior = scrollBehavior,
                                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                                onNavigationClick = { navController.popBackStack() },
                            )
                        }) {
                            FormRenderer(
                                schema = pickerSchema,
                                modifier = Modifier.padding(16.dp),
                                onSubmit = { values ->
                                    val result = (values["result"] as? FormValue.Text)?.value.orEmpty()
                                    activePickerKey?.let { key -> pendingResult = FormFieldResult(key, result) }
                                    navController.popBackStack()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
