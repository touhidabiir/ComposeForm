package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormBorder
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormOption
import com.touhid.composeform.formbuilder.schema.FormRadioAppearance
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormValidatorTest {

    private fun schemaOf(vararg fields: FormField) = FormSchema(fields.toList())

    @Test
    fun `text and submit fields are never flagged`() {
        val schema = schemaOf(
            FormField.Text(key = "heading", label = "Hi"),
            FormField.Submit(key = "submit", label = "Go"),
        )
        assertTrue(validate(schema, emptyMap()).isEmpty())
    }

    @Test
    fun `required inputBox blank fails with default message`() {
        val field = FormField.InputBox(key = "name", label = "Name", required = true)
        val schema = schemaOf(field)
        val errors = validate(schema, mapOf("name" to FormValue.Text("")))
        assertEquals("This field is required", errors["name"])
    }

    @Test
    fun `required inputBox filled passes`() {
        val field = FormField.InputBox(key = "name", label = "Name", required = true)
        val schema = schemaOf(field)
        val errors = validate(schema, mapOf("name" to FormValue.Text("Jane")))
        assertNull(errors["name"])
    }

    @Test
    fun `inputBox below minLength fails`() {
        val field = FormField.InputBox(key = "pw", label = "Password", minLength = 8)
        val schema = schemaOf(field)
        val errors = validate(schema, mapOf("pw" to FormValue.Text("short")))
        assertEquals("Must be at least 8 characters", errors["pw"])
    }

    @Test
    fun `inputBox above maxLength fails`() {
        val field = FormField.InputBox(key = "bio", label = "Bio", maxLength = 5)
        val schema = schemaOf(field)
        val errors = validate(schema, mapOf("bio" to FormValue.Text("toolong")))
        assertEquals("Must be at most 5 characters", errors["bio"])
    }

    @Test
    fun `inputBox pattern mismatch uses custom errorMessage`() {
        val field = FormField.InputBox(
            key = "email",
            label = "Email",
            pattern = "^[^@]+@[^@]+$",
            errorMessage = "Enter a valid email address",
        )
        val schema = schemaOf(field)
        val errors = validate(schema, mapOf("email" to FormValue.Text("not-an-email")))
        assertEquals("Enter a valid email address", errors["email"])
    }

    @Test
    fun `inputBox pattern mismatch falls back to default message`() {
        val field = FormField.InputBox(key = "email", label = "Email", pattern = "^[^@]+@[^@]+$")
        val schema = schemaOf(field)
        val errors = validate(schema, mapOf("email" to FormValue.Text("not-an-email")))
        assertEquals("Invalid format", errors["email"])
    }

    @Test
    fun `inputBox pattern is not checked when value is blank and not required`() {
        val field = FormField.InputBox(key = "email", label = "Email", pattern = "^[^@]+@[^@]+$")
        val schema = schemaOf(field)
        val errors = validate(schema, mapOf("email" to FormValue.Text("")))
        assertNull(errors["email"])
    }

    @Test
    fun `required checkbox unchecked fails, checked passes`() {
        val field = FormField.Checkbox(key = "terms", label = "Accept", required = true)
        val schema = schemaOf(field)
        assertEquals("This field is required", validate(schema, mapOf("terms" to FormValue.Text("false")))["terms"])
        assertNull(validate(schema, mapOf("terms" to FormValue.Text("true")))["terms"])
    }

    @Test
    fun `required switch off fails, on passes`() {
        val field = FormField.Switch(key = "notify", label = "Notify", required = true)
        val schema = schemaOf(field)
        assertEquals("This field is required", validate(schema, mapOf("notify" to FormValue.Text("false")))["notify"])
        assertNull(validate(schema, mapOf("notify" to FormValue.Text("true")))["notify"])
    }

    @Test
    fun `required radio with no selection fails, with selection passes`() {
        val options = listOf(FormOption("m", "Male"), FormOption("f", "Female"))
        val field = FormField.Radio(key = "gender", label = "Gender", required = true, options = options)
        val schema = schemaOf(field)
        assertEquals("Please select an option", validate(schema, emptyMap())["gender"])
        assertNull(validate(schema, mapOf("gender" to FormValue.Option("m", "Male")))["gender"])
    }

    @Test
    fun `radio appearance does not affect validation`() {
        val options = listOf(FormOption("m", "Male"), FormOption("f", "Female"))
        for (appearance in FormRadioAppearance.entries) {
            val field = FormField.Radio(
                key = "gender",
                label = "Gender",
                required = true,
                options = options,
                appearance = appearance,
            )
            val schema = schemaOf(field)
            assertEquals("Please select an option", validate(schema, emptyMap())["gender"])
            assertNull(validate(schema, mapOf("gender" to FormValue.Option("m", "Male")))["gender"])
        }
    }

    @Test
    fun `border does not affect validation`() {
        val border = FormBorder(enabled = true, color = "#D81B60", width = 2, radius = 8)
        val options = listOf(FormOption("m", "Male", border = border), FormOption("f", "Female"))
        val field = FormField.Radio(key = "gender", label = "Gender", required = true, options = options, border = border)
        val schema = schemaOf(field)
        assertEquals("Please select an option", validate(schema, emptyMap())["gender"])
        assertNull(validate(schema, mapOf("gender" to FormValue.Option("m", "Male")))["gender"])
    }

    @Test
    fun `required dropdown with no selection fails`() {
        val options = listOf(FormOption("bd", "Bangladesh"))
        val field = FormField.Dropdown(key = "country", label = "Country", required = true, options = options)
        val schema = schemaOf(field)
        assertEquals("Please select an option", validate(schema, emptyMap())["country"])
    }

    @Test
    fun `required checkboxGroup with empty selection fails, with one selection passes`() {
        val options = listOf(FormOption("music", "Music"), FormOption("books", "Books"))
        val field = FormField.CheckboxGroup(key = "interests", label = "Interests", required = true, options = options)
        val schema = schemaOf(field)
        assertEquals(
            "Please select at least one option",
            validate(schema, mapOf("interests" to FormValue.Options(emptyList())))["interests"],
        )
        assertNull(
            validate(
                schema,
                mapOf("interests" to FormValue.Options(listOf(FormValue.Option(options[0].id, options[0].value)))),
            )["interests"],
        )
    }

    @Test
    fun `optional checkboxGroup with empty selection passes`() {
        val field = FormField.CheckboxGroup(key = "interests", label = "Interests", required = false, options = emptyList())
        val schema = schemaOf(field)
        assertNull(validate(schema, mapOf("interests" to FormValue.Options(emptyList())))["interests"])
    }
}
