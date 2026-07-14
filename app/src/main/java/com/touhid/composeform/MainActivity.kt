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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.layout.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.formbuilder.FormFieldResult
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.parseFormSchema
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.singleAnswerValue
import kotlinx.coroutines.launch

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

private val MAIN_FORM_JSON = """
{
  "screenTitle": "ComposeForm Demo",
  "fields": [
    {
      "type": "inputBox", "key": "location", "label": "Location Type", "required": true,
      "margin": { "bottom": 8 },
      "pickerScreen": {
        "screenTitle": "Select Location Type",
        "fields": [
          {
            "type": "radio", "key": "location_type", "required": true, "orientation": "vertical", "appearance": "check",
            "options": [
              { "id": "inside_market", "value": "মার্কেটের ভেতরে" },
              { "id": "beside_road", "value": "রাস্তার পাশে" },
              { "id": "main_road", "value": "মেইন রোড" },
              { "id": "road_corner", "value": "মোড়ের কাছে" },
              { "id": "residential", "value": "আবাসিক এলাকা" }
            ]
          },
          { "type": "submit", "key": "submit", "label": "Confirm" }
        ]
      }
    },
    {
      "type": "inputBox", "key": "propertyType", "label": "Property Type", "required": true,
      "margin": { "bottom": 8 },
      "pickerScreen": {
        "screenTitle": "Select Property Type",
        "fields": [
          {
            "type": "radio", "key": "property_type", "required": true, "orientation": "vertical", "appearance": "check",
            "options": [
              { "id": "shop", "value": "দোকান" },
              { "id": "office", "value": "অফিস" },
              { "id": "warehouse", "value": "গুদাম" },
              { "id": "apartment", "value": "এপার্টমেন্ট" }
            ]
          },
          { "type": "submit", "key": "submit", "label": "Confirm" }
        ]
      }
    },
    {
      "type": "submit", "key": "submit", "label": "Submit",
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
                var activePickerSchema by remember { mutableStateOf<FormSchema?>(null) }
                var pendingResult by remember { mutableStateOf<FormFieldResult?>(null) }

                NavHost(navController = navController, startDestination = "form") {
                    composable("form") {
                        val schema = remember { parseFormSchema(MAIN_FORM_JSON) }
                        val snackbarHostState = rememberAppSnackbarHostState()
                        val scope = rememberCoroutineScope()

                        LaunchedEffect(pendingResult) {
                            if (pendingResult != null) {
                                activePickerKey = null
                                pendingResult = null
                            }
                        }

                        AppScaffold(
                            topBar = { scrollBehavior ->
                                AppTopBar(
                                    title = schema.screenTitle ?: "ComposeForm Demo",
                                    scrollBehavior = scrollBehavior,
                                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                                    onNavigationClick = { finish() },
                                )
                            },
                            snackbarHostState = snackbarHostState,
                        ) {
                            FormRenderer(
                                schema = schema,
                                modifier = Modifier.padding(16.dp),
                                pendingResult = pendingResult,
                                onPickerFieldClick = { key, pickerSchema ->
                                    activePickerKey = key
                                    activePickerSchema = pickerSchema
                                    navController.navigate("picker")
                                },
                                onValidationError = { message ->
                                    scope.launch { snackbarHostState.showMessage(message) }
                                },
                                onSubmit = { values -> Log.d("FormDemo", values.toString()) },
                            )
                        }
                    }

                    composable("picker") {
                        val pickerSchema = activePickerSchema
                        if (pickerSchema == null) {
                            LaunchedEffect(Unit) { navController.popBackStack() }
                        } else {
                            val snackbarHostState = rememberAppSnackbarHostState()
                            val scope = rememberCoroutineScope()

                            AppScaffold(
                                topBar = { scrollBehavior ->
                                    AppTopBar(
                                        title = pickerSchema.screenTitle ?: "Select a value",
                                        scrollBehavior = scrollBehavior,
                                        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                                        onNavigationClick = { navController.popBackStack() },
                                    )
                                },
                                snackbarHostState = snackbarHostState,
                            ) {
                                FormRenderer(
                                    schema = pickerSchema,
                                    modifier = Modifier.padding(16.dp),
                                    onValidationError = { message ->
                                        scope.launch { snackbarHostState.showMessage(message) }
                                    },
                                    onSubmit = { values ->
                                        val result = pickerSchema.singleAnswerValue(values)
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
}
