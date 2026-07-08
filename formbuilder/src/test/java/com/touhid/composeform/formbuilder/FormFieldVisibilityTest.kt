package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormValue
import com.touhid.composeform.formbuilder.schema.FormVisibilityCondition
import com.touhid.composeform.formbuilder.schema.FormVisibilityOperator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormFieldVisibilityTest {

    @Test
    fun `field with no visibleWhen is always visible`() {
        val field = FormField.Text(key = "heading", label = "Hi")
        assertTrue(field.isVisible(emptyMap()))
    }

    @Test
    fun `equals matches Option id, not value`() {
        val field = field(FormVisibilityOperator.Equals, listOf("male"))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
        assertFalse(field.isVisible(mapOf("gender" to FormValue.Option("female", "Female"))))
    }

    @Test
    fun `equals matches Text value`() {
        val field = field(FormVisibilityOperator.Equals, listOf("yes"))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Text("yes"))))
        assertFalse(field.isVisible(mapOf("gender" to FormValue.Text("no"))))
    }

    @Test
    fun `notEquals is the inverse of equals`() {
        val field = field(FormVisibilityOperator.NotEquals, listOf("male"))
        assertFalse(field.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Option("female", "Female"))))
    }

    @Test
    fun `in matches any acceptable value, empty list never matches`() {
        val field = field(FormVisibilityOperator.In, listOf("male", "others"))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Option("others", "Others"))))
        assertFalse(field.isVisible(mapOf("gender" to FormValue.Option("female", "Female"))))

        val neverMatches = field(FormVisibilityOperator.In, emptyList())
        assertFalse(neverMatches.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
    }

    @Test
    fun `absent trigger value hides field for every operator, including notEquals`() {
        val equals = field(FormVisibilityOperator.Equals, listOf("male"))
        val notEquals = field(FormVisibilityOperator.NotEquals, listOf("male"))
        val inOp = field(FormVisibilityOperator.In, listOf("male"))

        assertFalse(equals.isVisible(emptyMap()))
        assertFalse(notEquals.isVisible(emptyMap()))
        assertFalse(inOp.isVisible(emptyMap()))
    }

    @Test
    fun `values beyond index 0 are ignored for equals and notEquals`() {
        val equals = field(FormVisibilityOperator.Equals, listOf("male", "female"))
        assertTrue(equals.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
        assertFalse(equals.isVisible(mapOf("gender" to FormValue.Option("female", "Female"))))

        val notEquals = field(FormVisibilityOperator.NotEquals, listOf("male", "female"))
        assertFalse(notEquals.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
        assertTrue(notEquals.isVisible(mapOf("gender" to FormValue.Option("female", "Female"))))
    }

    @Test
    fun `Options trigger with multiple selections - equals requires exactly one match, in matches any`() {
        val equals = field(FormVisibilityOperator.Equals, listOf("music"), triggerKey = "interests")
        val inOp = field(FormVisibilityOperator.In, listOf("music", "books"), triggerKey = "interests")

        val multiSelected = FormValue.Options(listOf(FormValue.Option("music", "Music"), FormValue.Option("books", "Books")))
        assertFalse(equals.isVisible(mapOf("interests" to multiSelected)))
        assertTrue(inOp.isVisible(mapOf("interests" to multiSelected)))

        val singleSelected = FormValue.Options(listOf(FormValue.Option("music", "Music")))
        assertTrue(equals.isVisible(mapOf("interests" to singleSelected)))
    }

    @Test
    fun `empty Options selection is treated as absent`() {
        val notEquals = field(FormVisibilityOperator.NotEquals, listOf("music"), triggerKey = "interests")
        assertFalse(notEquals.isVisible(mapOf("interests" to FormValue.Options(emptyList()))))
    }

    private fun field(
        operator: FormVisibilityOperator,
        values: List<String>,
        triggerKey: String = "gender",
    ) = FormField.Radio(
        key = "newsletter",
        label = "Subscribe?",
        options = emptyList(),
        visibleWhen = FormVisibilityCondition(key = triggerKey, operator = operator, values = values),
    )
}
