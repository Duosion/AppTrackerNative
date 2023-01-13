package com.example.apptracker.ui.components.barChart

import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.Easing
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle

data class BarChartStyle(
    val barsWidth: Dp = 20.dp,
    val barsBorderSize: Dp = 10.dp,
    val labelTextStyle: TextStyle,
    val labelTweenDurationMillis: Int = 350,
    val labelTweenEase: Easing = EaseOutQuad
) {
    companion object {
        @Composable
        fun default(): BarChartStyle {
            return BarChartStyle(
                labelTextStyle = MaterialTheme.typography.labelLarge
            )
        }
    }
}
