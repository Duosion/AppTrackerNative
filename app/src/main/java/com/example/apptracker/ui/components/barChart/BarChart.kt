package com.example.apptracker.ui.components.barChart

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    bars: List<BarChartBar>,
    colors: BarChartColors = BarChartColors.default(),
    padding: BarChartPadding = BarChartPadding(),
    style: BarChartStyle = BarChartStyle.default(),
    onSelectedBarChange: (Int) -> Unit,
    selectedBar: Int = bars.count() - 1
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
                        val offset = (spaceBetweenLines * lineNumber).toFloat()
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
                    padding,
                    style = style
                )
            }

        }
        Divider(
            modifier = Modifier.padding(vertical = padding.dividerPadding, horizontal = paddingBetweenBars)
        )
        // labels

        // select last label by default

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
                    text = bar.label,
                    style = style,
                    selected = selectedBar == index,
                    onClick = {
                        onSelectedBarChange(index)
                    }
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
    text: String,
    colors: BarChartColors = BarChartColors.default(),
    padding: BarChartPadding = BarChartPadding(),
    style: BarChartStyle = BarChartStyle.default(),
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            IconButton(
                onClick = onClick,
                colors = if(selected) IconButtonDefaults.filledTonalIconButtonColors() else IconButtonDefaults.iconButtonColors()
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
    }
}
