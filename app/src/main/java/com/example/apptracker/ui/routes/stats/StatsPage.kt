package com.example.apptracker.ui.routes.stats

import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.barChart.BarChart
import com.example.apptracker.ui.components.barChart.BarChartBar
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatsPage(
    navController: NavController,
    viewModel: StatsViewModel
) {
    val screenState by viewModel.state.collectAsState()
    val usageTimes = screenState.usageTime

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.stats_nav_bar_button_name)) }
            )
        }
    ) { padding ->
        Divider()

        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {/*TODO*/}
            ){
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "App Usage Time",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    var largestUsageTime = 0L

                    usageTimes.forEach {
                        largestUsageTime = largestUsageTime.coerceAtLeast(it.combinedUsageTime / 1000)
                    }

                    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")

                    BarChart(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .height(200.dp)
                            .fillMaxWidth(),
                        bars = screenState.usageTime.map {
                            val usageTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(it.combinedUsageTime)
                            BarChartBar(
                                fraction = usageTimeSeconds.toFloat() / largestUsageTime,
                                headline = DateUtils.formatElapsedTime(usageTimeSeconds),
                                supporting = it.timestamp.date.format(dateFormatter)
                            )
                        }
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsCard(
    app: StatsScreenApp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        StatsListItem(
            app = app
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsListItem(
    app: StatsScreenApp
) {
    val label = app.label
    ListItem(
        modifier = Modifier.fillMaxSize(),
        headlineText = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = label!!,
                    textAlign = TextAlign.Start
                )
                ElapsedTimeText(
                    modifier = Modifier.fillMaxWidth(),
                    elapsedTime = app.usageInfo.usageTime,
                    textAlign = TextAlign.End
                )
            }

        },
        leadingContent = {
            Image(
                modifier = Modifier.size(48.dp),
                painter = rememberDrawablePainter(drawable = app.icon),
                contentDescription = label,
                contentScale = ContentScale.FillHeight,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ElapsedTimeText(
    // the elapsed time in milliseconds
    modifier: Modifier = Modifier,
    elapsedTime: Long,
    textAlign: TextAlign? = null
) {

    val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)

    val formatted = String.format(stringResource(id = R.string.stats_elapsed_time_format_string),
        hours,
        TimeUnit.MILLISECONDS.toMinutes(elapsedTime) - TimeUnit.HOURS.toMinutes(hours)
    )
    
    Text(
        modifier = modifier,
        text = formatted,
        textAlign = textAlign
    )

}