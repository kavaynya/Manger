package com.san.kir.core.compose

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

public object Styles {
    public val secondaryText: TextStyle = TextStyle(fontSize = 13.sp)
}

public object Colors {
    @Composable
    public fun loginButton(): ButtonColors =
        ButtonDefaults.buttonColors(containerColor = Color.Green)

    @Composable
    public fun logoutButton(): ButtonColors =
        ButtonDefaults.buttonColors(containerColor = Color.Red)
}

