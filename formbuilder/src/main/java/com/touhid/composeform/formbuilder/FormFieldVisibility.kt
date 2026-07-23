package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue
import com.touhid.composeform.formbuilder.schema.FormVisibilityCondition
import com.touhid.composeform.formbuilder.schema.FormVisibilityOperator

internal fun FormField.isVisible(values: Map<String, FormValue>): Boolean {
    val condition = visibleWhen ?: return true
    return condition.isSatisfiedBy(values[condition.key])
}

private fun FormVisibilityCondition.isSatisfiedBy(triggerValue: FormValue?): Boolean {
    val tokens = triggerValue.toComparableTokens()
    if (tokens.isEmpty()) return false
    return when (operator) {
        FormVisibilityOperator.Equals -> tokens.singleOrNull() == values.firstOrNull()
        FormVisibilityOperator.NotEquals -> tokens.singleOrNull() != values.firstOrNull()
        FormVisibilityOperator.In -> tokens.any { it in values }
    }
}

private fun FormValue?.toComparableTokens(): List<String> = when (this) {
    null -> emptyList()
    is FormValue.Text -> listOf(value)
    is FormValue.Option -> listOf(id)
    is FormValue.Options -> selected.map { it.id }
    is FormValue.Image -> listOf(url)
}

/**
 * Repeatedly strips values for fields hidden given the current map, until a fixed point, so a
 * hidden field's stale value never lingers in state and multi-hop visibility chains cascade
 * correctly in one pass.
 */
internal fun Map<String, FormValue>.retainVisible(schema: FormSchema): Map<String, FormValue> {
    var current = this
    while (true) {
        val hiddenKeys = schema.fields.filterNot { it.isVisible(current) }.map { it.key }.toSet()
        val next = current - hiddenKeys
        if (next == current) return current
        current = next
    }
}
