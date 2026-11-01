package com.touhid.composeform.session.domain

// Bangladeshi mobile numbers in their 11-digit local form: 01, then an operator digit 3-9, then
// eight more digits (01712345678). The sign-in API takes this form.
object BangladeshPhoneNumber {

    const val LENGTH = 11

    private val pattern = Regex("^01[3-9]\\d{8}$")

    fun isValid(number: String): Boolean = pattern.matches(number)

    // 01712345678 -> +8801712345678, for display.
    fun toInternational(number: String): String = "+88$number"
}
