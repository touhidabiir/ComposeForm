package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormOption
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue
import com.touhid.composeform.formbuilder.schema.FormVisibilityCondition
import com.touhid.composeform.formbuilder.schema.FormVisibilityOperator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormStateTest {

    private fun schemaOf(vararg fields: FormField) = FormSchema(fields.toList())

    private val genderOptions = listOf(FormOption("male", "Male"), FormOption("female", "Female"))

    private fun newsletterField(triggerKey: String = "gender", equalsValue: String = "male") = FormField.Radio(
        key = "newsletter",
        label = "Subscribe?",
        required = true,
        options = listOf(FormOption("yes", "Yes"), FormOption("no", "No")),
        visibleWhen = FormVisibilityCondition(triggerKey, FormVisibilityOperator.NotEquals, listOf(equalsValue)),
    )

    @Test
    fun `hiding a field clears its value entirely from state`() {
        val gender = FormField.Radio(key = "gender", label = "Gender", options = genderOptions)
        val newsletter = newsletterField()
        val schema = schemaOf(gender, newsletter)
        val state = FormState(schema, emptyMap(), emptySet())

        state.update("gender", FormValue.Option("female", "Female"))
        state.update("newsletter", FormValue.Option("yes", "Yes"))
        assertTrue("newsletter" in state.values)

        state.update("gender", FormValue.Option("male", "Male"))
        assertFalse("newsletter" in state.values)
    }

    @Test
    fun `hidden required field produces no validation error`() {
        val gender = FormField.Radio(key = "gender", label = "Gender", options = genderOptions)
        val newsletter = newsletterField()
        val schema = schemaOf(gender, newsletter)
        val state = FormState(schema, emptyMap(), emptySet())

        state.update("gender", FormValue.Option("male", "Male"))
        val errors = validate(schema, state.values)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `field re-shown after being hidden starts absent, no stale value`() {
        val gender = FormField.Radio(key = "gender", label = "Gender", options = genderOptions)
        val newsletter = newsletterField()
        val schema = schemaOf(gender, newsletter)
        val state = FormState(schema, emptyMap(), emptySet())

        state.update("gender", FormValue.Option("female", "Female"))
        state.update("newsletter", FormValue.Option("yes", "Yes"))
        state.update("gender", FormValue.Option("male", "Male"))
        state.update("gender", FormValue.Option("female", "Female"))

        assertFalse("newsletter" in state.values)
    }

    @Test
    fun `two fields depending on the same trigger both toggle on one update`() {
        val gender = FormField.Radio(key = "gender", label = "Gender", options = genderOptions)
        val newsletterA = newsletterField().let { it.copy(key = "newsletterA") }
        val newsletterB = newsletterField().let { it.copy(key = "newsletterB") }
        val schema = schemaOf(gender, newsletterA, newsletterB)
        val state = FormState(schema, emptyMap(), emptySet())

        state.update("gender", FormValue.Option("female", "Female"))
        state.update("newsletterA", FormValue.Option("yes", "Yes"))
        state.update("newsletterB", FormValue.Option("no", "No"))
        assertTrue("newsletterA" in state.values)
        assertTrue("newsletterB" in state.values)

        state.update("gender", FormValue.Option("male", "Male"))
        assertFalse("newsletterA" in state.values)
        assertFalse("newsletterB" in state.values)
    }

    @Test
    fun `a 3-field dependency chain cascades correctly in one update`() {
        val a = FormField.Radio(key = "a", label = "A", options = genderOptions)
        val b = newsletterField(triggerKey = "a").let { it.copy(key = "b") }
        val c = FormField.Radio(
            key = "c",
            label = "C",
            options = listOf(FormOption("yes", "Yes"), FormOption("no", "No")),
            visibleWhen = FormVisibilityCondition("b", FormVisibilityOperator.Equals, listOf("yes")),
        )
        val schema = schemaOf(a, b, c)
        val state = FormState(schema, emptyMap(), emptySet())

        state.update("a", FormValue.Option("female", "Female"))
        state.update("b", FormValue.Option("yes", "Yes"))
        state.update("c", FormValue.Option("yes", "Yes"))
        assertTrue("b" in state.values)
        assertTrue("c" in state.values)

        state.update("a", FormValue.Option("male", "Male"))
        assertFalse("b" in state.values)
        assertFalse("c" in state.values)
    }

    @Test
    fun `touched is pruned for a field cleared by hiding`() {
        val gender = FormField.Radio(key = "gender", label = "Gender", options = genderOptions)
        val newsletter = newsletterField()
        val schema = schemaOf(gender, newsletter)
        val state = FormState(schema, emptyMap(), emptySet())

        state.update("gender", FormValue.Option("female", "Female"))
        state.update("newsletter", FormValue.Option("yes", "Yes"))
        assertTrue("newsletter" in state.touched)

        state.update("gender", FormValue.Option("male", "Male"))
        assertFalse("newsletter" in state.touched)
    }

    @Test
    fun `initial values seeded from defaults are already filtered by visibility`() {
        val gender = FormField.Radio(key = "gender", label = "Gender", options = genderOptions.map { it.copy(default = it.id == "male") })
        val newsletter = newsletterField()
        val schema = schemaOf(gender, newsletter)
        val state = FormState(
            schema,
            initialValues = mapOf("gender" to FormValue.Option("male", "Male"), "newsletter" to FormValue.Option("yes", "Yes")),
            initialTouched = emptySet(),
        )

        assertEquals(FormValue.Option("male", "Male"), state.values["gender"])
        assertFalse("newsletter" in state.values)
    }
}
