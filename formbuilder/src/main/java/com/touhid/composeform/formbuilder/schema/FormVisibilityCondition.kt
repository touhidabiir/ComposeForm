package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

/**
 * A single-condition visibility rule: the field carrying this on its `visibleWhen` property is
 * visible only when the field identified by [key] currently holds a value matching
 * [operator]/[values].
 *
 * [values] is always used as a full set, for both operators: [FormVisibilityOperator.Equals] is
 * satisfied when the trigger field's value(s) overlap with any entry in [values]; deliberately
 * one operator whether [values] has one entry or many, rather than one shape that behaves
 * differently depending on how many entries it happens to have. [FormVisibilityOperator.NotEquals]
 * is satisfied when there is no such overlap.
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
