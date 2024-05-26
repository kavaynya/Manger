package com.san.kir.statistic.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration


internal fun AnnotatedString.Builder.appendAndHighlightDigits(text: String) {
    append(text)
    highlightDigits(text)
}

private fun AnnotatedString.Builder.highlightDigits(text: String) {
    text.forEachIndexed { index, char ->
        if (char.isDigit()) {
            addStyle(SpanStyle(fontWeight = FontWeight.Bold), index, index + 1)
        }
    }
}

private fun AnnotatedString.Builder.underline(text: String, forUnderline: Array<out String>) {
    for (str in forUnderline) {
        val start = text.indexOf(str)
        addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, start + str.length)
    }
}

internal fun buildPreparedString(text: String, vararg forUnderline: String) = buildAnnotatedString {
    append(text)
    underline(text, forUnderline)
    highlightDigits(text)
}
