package com.touhid.composeform.formbuilder

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.formbuilder.schema.FormField

val JSON_FORM = """
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
      "pickerScreen": {
        "screenTitle": "Select a name",
        "fields": [
          {
            "type": "inputBox",
            "key": "result",
            "label": "Name",
            "required": true
          },
          {
            "type": "submit",
            "key": "submit",
            "label": "Confirm"
          }
        ]
      },
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
      "visibleWhen": {
        "key": "gender",
        "operator": "notEquals",
        "values": ["male"]
      },
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
      "type": "imagePicker",
      "key": "idPhoto",
      "label": "ID Photo",
      "required": true,
      "uploadUrl": "demo://upload/id-photo",
      "screenTitle": "Take a photo of your ID",
      "instructionsHtml": "<b>Take a clear photo</b><p>Follow these tips for a good photo:</p><ul><li>Place the document on a flat surface</li><li>Shoot in good lighting</li><li>Keep the whole document inside the frame</li></ul>",
      "margin": {
        "top": 8,
        "bottom": 8,
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
    val schema = parseFormSchema(JSON_FORM)
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

val SPECIFIC_JSON_FORM = """
{
  "questions": [
    {
      "key": "q1",
      "question": "দোকানটি কি মার্কেটের ভিতরে অবস্থিত?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": false,
      "answers": [
        {"key": "yes", "value": "হ্যাঁ"},
        {"key": "no", "value": "না"}
      ]
    },
    {
      "key": "q2",
      "question": "মার্কেট বা দোকানটি কি শীতাতপ নিয়ন্ত্রিত?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": false,
      "answers": [
        {"key": "yes", "value": "হ্যাঁ"},
        {"key": "no", "value": "না"}
      ]
    },
    {
      "key": "q3",
      "question": "দোকানটির প্রবেশ পথে দরজা কি ধরণের?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": false,
      "answers": [
        {"key": "glass", "value": "গ্লাস"},
        {"key": "wood", "value": "কাঠ"},
        {"key": "thai_glass", "value": "থাই গ্লাস"},
        {"key": "none", "value": "কোনো দরজা নেই"}
      ]
    },
    {
      "key": "q4",
      "question": "দোকানের বাহিরে কি কোন আলোকিত সাইন বোর্ড আছে?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": false,
      "answers": [
        {"key": "yes", "value": "হ্যাঁ", "show": ["q5"]},
        {"key": "no", "value": "না", "hide": ["q5"]}
      ]
    },
    {
      "key": "q5",
      "question": "দোকানটির আলোকিত সাইন বোর্ড কি পরিষ্কার পরিছন্ন?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": true,
      "depends_on": {"question": "q4", "answer": "yes"},
      "answers": [
        {"key": "yes", "value": "হ্যাঁ"},
        {"key": "no", "value": "না"}
      ]
    },
    {
      "key": "q6",
      "question": "দোকানটির ভেতরে কয়টি টিউব লাইট আছে?",
      "type": "single_choice",
      "orientation": "vertical",
      "is_dependent": false,
      "answers": [
        {"key": "none", "value": "কোনো টিউবলাইট নেই"},
        {"key": "1_to_3", "value": "১ থেকে ৩"},
        {"key": "4_to_6", "value": "৪ থেকে ৬"},
        {"key": "7_to_10", "value": "৭ থেকে ১০"},
        {"key": "more_than_10", "value": "১০ এর অধিক"}
      ]
    },
    {
      "key": "q7",
      "question": "দোকানটির ভেতরে কয়টি স্পট লাইট আছে?",
      "type": "single_choice",
      "orientation": "vertical",
      "is_dependent": false,
      "answers": [
        {"key": "none", "value": "কোনো স্পটলাইট নেই"},
        {"key": "1_to_5", "value": "১ থেকে ৫"},
        {"key": "6_to_10", "value": "৬ থেকে ১০"},
        {"key": "11_to_15", "value": "১১ থেকে ১৫"},
        {"key": "more_than_15", "value": "১৫ এর অধিক"}
      ]
    },
    {
      "key": "q8",
      "question": "দোকানে আলাদা ক্যাশ কাউন্টার আছে?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": false,
      "answers": [
        {"key": "yes", "value": "হ্যাঁ"},
        {"key": "no", "value": "না"}
      ]
    },
    {
      "key": "q9",
      "question": "দোকানটি কি কার্ডের মাধ্যমে পেমেন্ট গ্রহণ করে?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": false,
      "answers": [
        {"key": "yes", "value": "হ্যাঁ"},
        {"key": "no", "value": "না"}
      ]
    },
    {
      "key": "q10",
      "question": "দোকানটি কি প্রিন্টেড বিল প্রদান করে?",
      "type": "single_choice",
      "orientation": "horizontal",
      "is_dependent": false,
      "answers": [
        {"key": "yes", "value": "হ্যাঁ"},
        {"key": "no", "value": "না"}
      ]
    }
  ]
}
""".trimIndent()

@Preview(name = "Light", showBackground = true, heightDp = 1800)
@Preview(name = "Dark", showBackground = true, heightDp = 1800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SpecificFormRendererPreview() {
    val parsed = parseSpecificFormSchema(SPECIFIC_JSON_FORM)
    val schema = parsed.copy(fields = parsed.fields + FormField.Submit(key = "submit", label = "সাবমিট"))
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
