package com.touhid.composeform.flow

import android.util.Log
import com.touhid.composeform.formbuilder.schema.FormOption
import kotlinx.coroutines.delay

/**
 * Stands in for a real backend: each "page" JSON below carries its own [FormPageResponse.nextFormUrl]
 * and [FormPageResponse.submitUrl], so [FormFlowViewModel] never hardcodes how many pages exist or
 * when to submit - it just follows what each response tells it.
 */
object DemoFormApi {

    const val START_URL = "demo://form/1"

    private val pages = mapOf(
        START_URL to PAGE_1_JSON,
        "demo://form/2" to PAGE_2_JSON,
        "demo://form/3" to PAGE_3_JSON,
    )

    suspend fun fetchPage(url: String): FormPageResponse {
        delay(400)
        val json = pages[url] ?: error("No demo page registered for url: $url")
        return parseFormPageResponse(json)
    }

    suspend fun submit(url: String, data: Map<String, String>) {
        delay(300)
        Log.d("FormFlow", "submit($url) -> $data")
    }

    // Stands in for a real multipart upload endpoint - the imagePicker field's uploadUrl points
    // here conceptually, but this demo never performs real networking, same as submit()/fetchPage().
    suspend fun uploadImage(uploadUrl: String, filePath: String): String {
        delay(1200)
        Log.d("FormFlow", "uploadImage($uploadUrl) -> $filePath")
        return "demo://uploads/${filePath.substringAfterLast('/')}"
    }

    // Stands in for a real backend call to fetch options for a field whose choices aren't known
    // upfront - referenced by a field's optionsUrl, exactly like fetchPage/submit stand in for
    // nextFormUrl/submitUrl. FormRenderer never calls this; only the app's picker flow does.
    suspend fun fetchOptions(url: String): List<FormOption> {
        delay(400)
        return DEMO_OPTIONS[url].orEmpty()
    }
}

// Distinct from the static options already listed in the page JSON below, so the demo shows
// fetched options getting appended after the static ones rather than duplicating them.
private val DEMO_OPTIONS: Map<String, List<FormOption>> = mapOf(
    "demo://options/location-types" to listOf(
        FormOption(id = "highway_side", value = "হাইওয়ের পাশে"),
        FormOption(id = "market_entrance", value = "মার্কেটের প্রবেশপথে"),
    ),
    "demo://options/property-types" to listOf(
        FormOption(id = "restaurant", value = "রেস্টুরেন্ট"),
        FormOption(id = "showroom", value = "শোরুম"),
    ),
    // A top-level (non-pickerScreen) example - fetched by FormFlowViewModel.loadPage when
    // PAGE_3_JSON loads, not by the picker orchestration in MainActivity.
    "demo://options/genders" to listOf(
        FormOption(id = "non_binary", value = "Non-binary"),
    ),
)

private val PAGE_1_JSON = """
{
  "schema": {
    "screenTitle": "Property Details",
    "fields": [
      {
        "type": "text", "key": "heading", "label": "Property Details",
        "style": { "size": 22, "weight": "bold" },
        "margin": { "bottom": 16 }
      },
      {
        "type": "inputBox", "key": "location", "label": "Location Type", "required": true,
        "margin": { "bottom": 8 },
        "pickerScreen": {
          "screenTitle": "Select Location Type",
          "fields": [
            {
              "type": "radio", "key": "location_type", "required": true, "orientation": "vertical", "appearance": "check",
              "optionsUrl": "demo://options/location-types",
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
              "optionsUrl": "demo://options/property-types",
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
        "type": "imagePicker", "key": "businessIdPhoto", "label": "ব্যবসার প্রমাণপত্রের ছবি", "required": true,
        "uploadUrl": "demo://upload/business-id-photo",
        "screenTitle": "ব্যবসার প্রমাণপত্র",
        "instructionsHtml": "<b>স্পষ্ট ছবি তুলুন</b><p>ভালো ছবি পেতে নিচের নির্দেশনা অনুসরণ করুন:</p><ul><li>কাগজটি সমতল জায়গায় রাখুন</li><li>পর্যাপ্ত আলোতে ছবি তুলুন</li><li>পুরো কাগজটি ফ্রেমের ভেতরে রাখুন</li></ul>",
        "takePhotoLabel": "📷 ছবি তুলুন",
        "nextStepLabel": "পরবর্তী ধাপ",
        "uploadingMessage": "অবস্থান নিশ্চিত করা হচ্ছে, অনুগ্রহ করে অপেক্ষা করুন",
        "margin": { "top": 16, "bottom": 8 }
      },
      {
        "type": "submit", "key": "submit", "label": "Next",
        "margin": { "top": 16 }
      }
    ]
  },
  "nextFormUrl": "demo://form/2",
  "submitUrl": null
}
""".trimIndent()

private val PAGE_2_JSON = """
{
  "schema": {
    "screenTitle": "Your Info",
    "fields": [
      {
        "type": "text", "key": "heading", "label": "Your Info",
        "style": { "size": 22, "weight": "bold" },
        "margin": { "bottom": 16 }
      },
      {
        "type": "inputBox", "key": "name", "label": "Name", "required": true, "inputType": "text",
        "margin": { "bottom": 8 }
      },
      {
        "type": "inputBox", "key": "email", "label": "Email", "required": true, "inputType": "email",
        "pattern": "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+${'$'}",
        "errorMessage": "Enter a valid email address",
        "margin": { "bottom": 8 }
      },
      {
        "type": "checkbox", "key": "acceptTerms", "label": "I agree to the terms and conditions", "required": true,
        "margin": { "top": 8, "bottom": 16 }
      },
      {
        "type": "submit", "key": "submit", "label": "Save & Continue"
      }
    ]
  },
  "nextFormUrl": "demo://form/3",
  "submitUrl": "demo://submit/page2"
}
""".trimIndent()

private val PAGE_3_JSON = """
{
  "schema": {
    "screenTitle": "Additional Details",
    "fields": [
      {
        "type": "text", "key": "heading", "label": "Additional Details",
        "style": { "size": 22, "weight": "bold" },
        "margin": { "bottom": 16 }
      },
      {
        "type": "inputBox", "key": "phone", "label": "Phone Number", "required": true, "inputType": "number",
        "pattern": "^[0-9]{10,15}${'$'}",
        "errorMessage": "Enter a valid phone number",
        "margin": { "bottom": 8 }
      },
      {
        "type": "radio", "key": "gender", "label": "Gender", "required": true, "orientation": "horizontal",
        "optionsUrl": "demo://options/genders",
        "options": [
          { "id": "male", "value": "Male" },
          { "id": "female", "value": "Female" },
          { "id": "others", "value": "Others" }
        ],
        "margin": { "top": 16, "bottom": 8 }
      },
      {
        "type": "checkboxGroup", "key": "interests", "label": "What do you like?", "required": false, "orientation": "vertical",
        "options": [
          { "id": "music", "value": "Music" },
          { "id": "books", "value": "Books" },
          { "id": "games", "value": "Games" }
        ],
        "margin": { "top": 16, "bottom": 16 }
      },
      {
        "type": "submit", "key": "submit", "label": "Finish"
      }
    ]
  },
  "nextFormUrl": null,
  "submitUrl": "demo://submit/final"
}
""".trimIndent()
