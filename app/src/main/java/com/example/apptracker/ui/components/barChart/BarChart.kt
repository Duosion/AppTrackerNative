package com.example.apptracker.ui.components.barChart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    bars: List<BarChartBar>,
    colors: BarChartColors = BarChartColors.default(),
    padding: BarChartPadding = BarChartPadding(),
    style: BarChartStyle = BarChartStyle.default()
) {
    val lineColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
    ) {
        val paddingBetweenBars = padding.paddingBetweenBars
        // bars
        Row(
            modifier = Modifier
                .padding(horizontal = paddingBetweenBars)
                .fillMaxWidth()
                .fillMaxHeight(0.7F)
                .drawBehind {

                    val spaceBetweenLines = 100

                    for (lineNumber in 0..(size.height.toInt() / spaceBetweenLines)) {
                        val offset = (spaceBetweenLines*lineNumber).toFloat()
                        drawLine(lineColor, Offset(0f, offset), Offset(size.width, offset))
                    }


                },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {

            bars.forEachIndexed { index, bar ->
                Bar(
                    bar.fraction,
                    1f / (bars.count() - index),
                    colors,
                    padding
                )
            }

        }
        Divider(
            modifier = Modifier.padding(vertical = padding.dividerPadding, horizontal = paddingBetweenBars)
        )
        // labels
        Row(
            modifier = Modifier
                .padding(horizontal = paddingBetweenBars)
                .fillMaxWidth()
                .fillMaxHeight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            bars.forEachIndexed{ index, bar ->
                BarLabel(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(1f / (bars.count() - index)),
                    headlineText = bar.headline,
                    supportingText = bar.supporting
                )
            }

        }

    }

}

@Composable
private fun Bar(
    fraction: Float,
    width: Float,
    colors: BarChartColors = BarChartColors.default(),
    padding: BarChartPadding = BarChartPadding(),
    style: BarChartStyle = BarChartStyle.default()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(width)
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .background(colors.barColor, RoundedCornerShape(style.barsBorderSize))
                .width(style.barsWidth)
                .fillMaxHeight(fraction),
        )
    }
}

@Composable
private fun BarLabel(
    modifier: Modifier = Modifier,
    headlineText: String,
    supportingText: String?,
    colors: BarChartColors = BarChartColors.default(),
    padding: BarChartPadding = BarChartPadding(),
    style: BarChartStyle = BarChartStyle.default()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = headlineText,
            color = colors.headlineColor,
            style = style.headlineTextStyle
        )

        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
