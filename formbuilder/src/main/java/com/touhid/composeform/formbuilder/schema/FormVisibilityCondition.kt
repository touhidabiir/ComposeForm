package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

/**
 * A single-condition visibility rule: the field carrying this on its `visibleWhen` property is
 * visible only when the field identified by [key] currently holds a value matching
 * [operator]/[values].
 *
 * For [FormVisibilityOperator.Equals] and [FormVisibilityOperator.NotEquals], only
 * `values.firstOrNull()` is consulted; additional entries are ignored. For
 * [FormVisibilityOperator.In], the full list is the acceptable set.
 *
 * If the field identified by [key] has no value at all (untouched with no default, or cleared
 * because it is itself currently hidden), the condition evaluates to not-satisfied for every
 * operator, including [FormVisibilityOperator.NotEquals].
 */
@Serializable
data class FormVisibilityCondition(
    val key: String,
    val operator: FormVisibilityOperator,
    val values: List<String>,
)
