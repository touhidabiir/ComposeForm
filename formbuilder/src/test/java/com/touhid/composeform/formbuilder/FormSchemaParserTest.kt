package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormOrientation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormSchemaParserTest {

    private val sampleJson = """
        {
          "fields": [
            { "type": "text", "key": "heading", "label": "Sign Up", "style": { "size": 24, "weight": "bold" } },
            { "type": "inputBox", "key": "name", "label": "Name", "required": true, "inputType": "text" },
            {
              "type": "inputBox", "key": "email", "label": "Email", "required": true, "inputType": "email",
              "pattern": "^[^@]+@[^@]+${'$'}", "errorMessage": "Enter a valid email address"
            },
            { "type": "checkbox", "key": "acceptTerms", "label": "I agree", "required": true },
            {
              "type": "checkboxGroup", "key": "interests", "label": "What do you like?",
              "options": [
                { "id": "music", "value": "Music", "default": true },
                { "id": "coding", "value": "Coding", "style": { "weight": "bold" } }
              ]
            },
            {
              "type": "radio", "key": "gender", "label": "Gender", "required": true, "orientation": "horizontal",
              "options": [
                { "id": "male", "value": "Male" },
                { "id": "female", "value": "Female", "style": { "color": "#D81B60" } }
              ]
            },
            { "type": "switch", "key": "notifications", "label": "Enable notifications" },
            {
              "type": "dropdown", "key": "country", "label": "Country", "required": true,
              "options": [ { "id": "bd", "value": "Bangladesh" }, { "id": "in", "value": "India" } ]
            },
            {
              "type": "submit", "key": "submit", "label": "Submit",
              "margin": { "top": 16, "bottom": 16, "left": 16, "right": 16 }
            }
          ]
        }
    """.trimIndent()

    @Test
    fun `parses all field types with correct discriminator dispatch`() {
        val schema = parseFormSchema(sampleJson)
        assertEquals(9, schema.fields.size)
        assertTrue(schema.fields[0] is FormField.Text)
        assertTrue(schema.fields[1] is FormField.InputBox)
        assertTrue(schema.fields[3] is FormField.Checkbox)
        assertTrue(schema.fields[4] is FormField.CheckboxGroup)
        assertTrue(schema.fields[5] is FormField.Radio)
        assertTrue(schema.fields[6] is FormField.Switch)
        assertTrue(schema.fields[7] is FormField.Dropdown)
        assertTrue(schema.fields[8] is FormField.Submit)
    }

    @Test
    fun `parses text field style`() {
        val heading = parseFormSchema(sampleJson).fields[0] as FormField.Text
        assertEquals(24, heading.style?.size)
        assertEquals("bold", heading.style?.weight)
    }

    @Test
    fun `parses inputBox pattern and errorMessage`() {
        val email = parseFormSchema(sampleJson).fields[2] as FormField.InputBox
        assertEquals("email", email.inputType)
        assertEquals("^[^@]+@[^@]+$", email.pattern)
        assertEquals("Enter a valid email address", email.errorMessage)
    }

    @Test
    fun `parses option default flag and per-option style`() {
        val interests = parseFormSchema(sampleJson).fields[4] as FormField.CheckboxGroup
        assertTrue(interests.options[0].default)
        assertEquals(false, interests.options[1].default)
        assertEquals("bold", interests.options[1].style?.weight)
    }

    @Test
    fun `parses orientation with lowercase serial names`() {
        val gender = parseFormSchema(sampleJson).fields[5] as FormField.Radio
        assertEquals(FormOrientation.Horizontal, gender.orientation)
    }

    @Test
    fun `parses submit margin`() {
        val submit = parseFormSchema(sampleJson).fields[8] as FormField.Submit
        assertEquals(16, submit.margin.top)
        assertEquals(16, submit.margin.left)
    }

    @Test
    fun `unspecified orientation defaults to vertical`() {
        val interests = parseFormSchema(sampleJson).fields[4] as FormField.CheckboxGroup
        assertEquals(FormOrientation.Vertical, interests.orientation)
    }

    @Test
    fun `unspecified style is null`() {
        val name = parseFormSchema(sampleJson).fields[1] as FormField.InputBox
        assertNull(name.style)
    }
}
