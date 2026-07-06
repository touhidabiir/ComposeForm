package com.touhid.composeform

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.parseFormSchema

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
      "pattern": "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
      "errorMessage": "Enter a valid email address",
      "margin": { "top": 8, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "inputBox", "key": "phone", "label": "Phone Number", "required": true, "inputType": "number",
      "pattern": "^[0-9]{10,15}$",
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
      "type": "text", "key": "heading", "label": "Gender",
      "style": { "size": 16, "weight": "normal" },
      "margin": { "top": 24, "bottom": 8, "left": 0, "right": 0 }
    },
    {
      "type": "radio", "key": "gender", "label": "", "required": true, "orientation": "horizontal",
      "options": [
        { "id": "male", "value": "Male" },
        { "id": "female", "value": "Female", "style": { "color": "#D81B60" } },
        { "id": "others", "value": "Others" }
      ],
      "margin": { "top": 0, "bottom": 8, "left": 0, "right": 0 }
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
        { "id": "music", "value": "Music", "default": true },
        { "id": "books", "value": "Books" },
        { "id": "games", "value": "Games" },
        { "id": "gossiping", "value": "Gossiping" },
        { "id": "coding", "value": "Coding", "style": { "weight": "bold" } }
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormTheme {
                val schema = remember { parseFormSchema(SAMPLE_FORM_JSON) }
                AppScaffold {
                    FormRenderer(
                        schema = schema,
                        onSubmit = { values -> Log.d("FormDemo", values.toString()) },
                    )
                }
            }
        }
    }
}
