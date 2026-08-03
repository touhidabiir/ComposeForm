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
    fun `equals matches any value in the list, empty list never matches`() {
        val field = field(FormVisibilityOperator.Equals, listOf("male", "others"))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Option("others", "Others"))))
        assertFalse(field.isVisible(mapOf("gender" to FormValue.Option("female", "Female"))))

        val neverMatches = field(FormVisibilityOperator.Equals, emptyList())
        assertFalse(neverMatches.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
    }

    @Test
    fun `notEquals excludes every value in the list`() {
        val field = field(FormVisibilityOperator.NotEquals, listOf("male", "others"))
        assertFalse(field.isVisible(mapOf("gender" to FormValue.Option("male", "Male"))))
        assertFalse(field.isVisible(mapOf("gender" to FormValue.Option("others", "Others"))))
        assertTrue(field.isVisible(mapOf("gender" to FormValue.Option("female", "Female"))))
    }

    @Test
    fun `absent trigger value hides field for every operator, including notEquals`() {
        val equals = field(FormVisibilityOperator.Equals, listOf("male"))
        val notEquals = field(FormVisibilityOperator.NotEquals, listOf("male"))

        assertFalse(equals.isVisible(emptyMap()))
        assertFalse(notEquals.isVisible(emptyMap()))
    }

    @Test
    fun `Options trigger with multiple selections matches on any overlap`() {
        val equals = field(FormVisibilityOperator.Equals, listOf("music"), triggerKey = "interests")
        val equalsMulti = field(FormVisibilityOperator.Equals, listOf("music", "books"), triggerKey = "interests")

        val multiSelected = FormValue.Options(listOf(FormValue.Option("music", "Music"), FormValue.Option("books", "Books")))
        assertTrue(equals.isVisible(mapOf("interests" to multiSelected)))
        assertTrue(equalsMulti.isVisible(mapOf("interests" to multiSelected)))

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
