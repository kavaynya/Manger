package com.san.kir.core.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

public object Fonts {
    public object Size {
        public val less: TextUnit = 13.sp
        public val default: TextUnit = 14.sp
        public val bigger: TextUnit = 16.sp
    }

    public object Style {
        public val bigBoldCenter: TextStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = Size.bigger,
            textAlign = TextAlign.Center
        )
    }

    public object Annotated {
        public fun bold(end: Int): List<AnnotatedString.Range<SpanStyle>> = listOf(
            AnnotatedString.Range(
                SpanStyle(fontWeight = FontWeight.Bold),
                start = 0,
                end = end
            )
        )

    }
}
