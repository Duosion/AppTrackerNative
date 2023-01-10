package com.example.apptracker.ui.components.barChart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class BarChartColors(
    val barColor: Color,
    val headlineColor: Color,
    val supportingColor: Color,
    val selectedLabelColor: Color,
) {
    companion object {
        @Composable
        fun default(): BarChartColors {
            return BarChartColors(
                barColor = MaterialTheme.colorScheme.primary,
                headlineColor = MaterialTheme.colorScheme.primary,
                supportingColor = MaterialTheme.colorScheme.onSurface,
                selectedLabelColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
