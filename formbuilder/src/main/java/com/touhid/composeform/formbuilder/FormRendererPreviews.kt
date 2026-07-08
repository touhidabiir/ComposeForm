package com.touhid.composeform.formbuilder

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

private val sampleFormJson = """
{
  "fields": [
    {
      "type": "text",
      "key": "heading",
      "label": "Sign Up",
      "style": {
        "size": 24,
        "weight": "bold"
      },
      "margin": {
        "top": 0,
        "bottom": 16,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "inputBox",
      "key": "name",
      "label": "Name",
      "required": true,
      "inputType": "text",
      "style": {
        "size": 16,
        "weight": "medium"
      },
      "margin": {
        "top": 8,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "inputBox",
      "key": "email",
      "label": "Email",
      "required": true,
      "inputType": "email",
      "pattern": "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+${'$'}{'$'}",
      "errorMessage": "Enter a valid email address",
      "margin": {
        "top": 8,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "inputBox",
      "key": "phone",
      "label": "Phone Number",
      "required": true,
      "inputType": "number",
      "pattern": "^[0-9]{10,15}${'$'}{'$'}",
      "errorMessage": "Enter a valid phone number",
      "margin": {
        "top": 8,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "inputBox",
      "key": "password",
      "label": "Password",
      "required": true,
      "inputType": "password",
      "minLength": 8,
      "errorMessage": "Must be at least 8 characters",
      "margin": {
        "top": 8,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "radio",
      "key": "gender",
      "label": "Gender",
      "required": true,
      "orientation": "horizontal",
      "appearance": "check",
      "options": [
        {
          "id": "male",
          "value": "Male",
          "default": true,
          "border": {
            "color": "#D81B60",
            "width": 2,
            "radius": 24
          },
          "margin": {
            "top": 12,
            "bottom": 4,
            "right": 4
          },
          "padding": {
            "top": 12,
            "bottom": 12,
            "left": 8,
            "right": 8
          }
        },
        {
          "id": "female",
          "value": "Female",
          "border": {
            "color": "#D81B60",
            "width": 2,
            "radius": 24
          },
          "margin": {
            "top": 12,
            "bottom": 4,
            "left": 4,
            "right": 4
          },
          "padding": {
            "top": 12,
            "bottom": 12,
            "left": 8,
            "right": 8
          }
        },
        {
          "id": "others",
          "value": "Others",
          "border": {
            "color": "#D81B60",
            "width": 2,
            "radius": 24
          },
          "margin": {
            "top": 12,
            "bottom": 4,
            "left": 4
          },
          "padding": {
            "top": 12,
            "bottom": 12,
            "left": 8,
            "right": 8
          }
        }
      ],
      "margin": {
        "top": 24,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "radio",
      "key": "newsletter",
      "label": "Subscribe to newsletter?",
      "required": true,
      "orientation": "horizontal",
      "appearance": "toggle",
      "options": [
        {
          "id": "yes",
          "value": "Yes",
          "default": true,
          "border": {
            "color": "#D81B60",
            "width": 2,
            "radius": 24
          },
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          },
          "padding": {
            "top": 12,
            "bottom": 12,
            "left": 16,
            "right": 16
          }
        },
        {
          "id": "no",
          "value": "No",
          "border": {
            "color": "#D81B60",
            "width": 2,
            "radius": 24
          },
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          },
          "padding": {
            "top": 12,
            "bottom": 12,
            "left": 16,
            "right": 16
          }
        }
      ],
      "margin": {
        "top": 24,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "radio",
      "key": "doorType",
      "label": "Door type",
      "required": true,
      "orientation": "vertical",
      "appearance": "check",
      "options": [
        {
          "id": "glass",
          "value": "Glass",
          "default": true,
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        },
        {
          "id": "wood",
          "value": "Wood",
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        },
        {
          "id": "thaiGlass",
          "value": "Thai Glass",
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        },
        {
          "id": "none",
          "value": "No door",
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        }
      ],
      "margin": {
        "top": 24,
        "bottom": 8,
        "left": 0,
        "right": 0
      },
      "padding": {
        "top": 8,
        "bottom": 8,
        "left": 8,
        "right": 8
      },
      "border": {
        "color": "#9E9E9E",
        "width": 1,
        "radius": 12
      }
    },
    {
      "type": "dropdown",
      "key": "country",
      "label": "Country",
      "required": true,
      "options": [
        {
          "id": "bangladesh",
          "value": "Bangladesh"
        },
        {
          "id": "india",
          "value": "India"
        },
        {
          "id": "pakistan",
          "value": "Pakistan"
        },
        {
          "id": "nepal",
          "value": "Nepal"
        }
      ],
      "margin": {
        "top": 8,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "checkboxGroup",
      "key": "interests",
      "label": "What do you like?",
      "required": false,
      "orientation": "vertical",
      "options": [
        {
          "id": "music",
          "value": "Music",
          "default": true,
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        },
        {
          "id": "books",
          "value": "Books",
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        },
        {
          "id": "games",
          "value": "Games",
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        },
        {
          "id": "gossiping",
          "value": "Gossiping",
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        },
        {
          "id": "coding",
          "value": "Coding",
          "style": {
            "weight": "bold"
          },
          "margin": {
            "top": 4,
            "bottom": 4,
            "left": 4,
            "right": 4
          }
        }
      ],
      "margin": {
        "top": 24,
        "bottom": 8,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "checkbox",
      "key": "acceptTerms",
      "label": "I agree to the terms and conditions",
      "required": true,
      "margin": {
        "top": 24,
        "bottom": 16,
        "left": 0,
        "right": 0
      }
    },
    {
      "type": "submit",
      "key": "submit",
      "label": "Submit",
      "style": {
        "size": 18,
        "weight": "bold",
        "color": "#FFFFFF"
      },
      "margin": {
        "top": 16,
        "bottom": 16,
        "left": 16,
        "right": 16
      }
    }
  ]
}
""".trimIndent()

@Preview(name = "Light", showBackground = true, heightDp = 1800)
@Preview(name = "Dark", showBackground = true, heightDp = 1800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FormRendererPreview() {
    val schema = parseFormSchema(sampleFormJson)
    ComposeFormTheme {
        AppScaffold {
            FormRenderer(
                schema = schema,
                modifier = Modifier.padding(AppSpacing.Medium),
                onSubmit = {},
            )
        }
    }
}
