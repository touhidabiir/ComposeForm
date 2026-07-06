package com.touhid.composeform.formbuilder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import com.touhid.composeform.designsystem.components.input.AppTextFieldType
import com.touhid.composeform.formbuilder.schema.FormTextStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormFieldMappersTest {

    @Test
    fun `null style maps to a no-op override`() {
        val override = (null as FormTextStyle?).toOverride()
        assertTrue(override.fontSize.isUnspecified)
        assertNull(override.fontWeight)
        assertTrue(override.color.isUnspecified())
    }

    @Test
    fun `size maps to sp`() {
        val override = FormTextStyle(size = 20).toOverride()
        assertEquals(20.sp, override.fontSize)
    }

    @Test
    fun `weight strings map to FontWeight, unrecognized maps to null`() {
        assertEquals(FontWeight.Light, FormTextStyle(weight = "light").toOverride().fontWeight)
        assertEquals(FontWeight.Normal, FormTextStyle(weight = "normal").toOverride().fontWeight)
        assertEquals(FontWeight.Medium, FormTextStyle(weight = "medium").toOverride().fontWeight)
        assertEquals(FontWeight.SemiBold, FormTextStyle(weight = "semibold").toOverride().fontWeight)
        assertEquals(FontWeight.Bold, FormTextStyle(weight = "bold").toOverride().fontWeight)
        assertNull(FormTextStyle(weight = "ultra-bold").toOverride().fontWeight)
    }

    @Test
    fun `six digit hex color gets full alpha`() {
        val override = FormTextStyle(color = "#D81B60").toOverride()
        assertEquals(Color(0xFFD81B60), override.color)
    }

    @Test
    fun `eight digit hex color keeps given alpha`() {
        val override = FormTextStyle(color = "#80D81B60").toOverride()
        assertEquals(Color(0x80D81B60), override.color)
    }

    @Test
    fun `invalid hex color falls back to unspecified`() {
        val override = FormTextStyle(color = "not-a-color").toOverride()
        assertTrue(override.color.isUnspecified())
    }

    @Test
    fun `inputType strings map correctly, unrecognized falls back to Text`() {
        assertEquals(AppTextFieldType.Number, "number".toAppTextFieldType())
        assertEquals(AppTextFieldType.Email, "email".toAppTextFieldType())
        assertEquals(AppTextFieldType.Password, "password".toAppTextFieldType())
        assertEquals(AppTextFieldType.Text, "text".toAppTextFieldType())
        assertEquals(AppTextFieldType.Text, "unknown".toAppTextFieldType())
    }

    private fun Color.isUnspecified(): Boolean = this == Color.Unspecified
}
