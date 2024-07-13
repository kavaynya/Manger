package com.san.kir.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle

public fun AnnotatedString.Builder.append(text: String, textStyle: TextStyle) {
    append(AnnotatedString(text, textStyle.toSpanStyle(), textStyle.toParagraphStyle()))
}

public fun AnnotatedString.Builder.append(text: String, spanStyle: SpanStyle) {
    append(AnnotatedString(text, spanStyle))
}

@Composable
public fun AnnotatedString.Builder.append(textId: Int, spanStyle: SpanStyle) {
    append(AnnotatedString(stringResource(textId), spanStyle))
}
