package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormValue
import org.junit.Assert.assertEquals
import org.junit.Test

class FormValuesJsonTest {

    @Test
    fun `round-trips every value type`() {
        val values = mapOf(
            "name" to FormValue.Text("Rahim"),
            "door" to FormValue.Option(id = "glass", value = "গ্লাস"),
            "items" to FormValue.Options(listOf(FormValue.Option("a", "A"), FormValue.Option("b", "B"))),
        )

        assertEquals(values, decodeFormValues(encodeFormValues(values)))
    }

    @Test
    fun `round-trips an empty form`() {
        assertEquals(emptyMap<String, FormValue>(), decodeFormValues(encodeFormValues(emptyMap())))
    }
}
