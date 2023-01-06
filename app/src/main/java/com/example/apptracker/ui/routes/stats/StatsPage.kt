package com.example.apptracker.ui.routes.stats

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.apptracker.ui.components.TrackedAppLastOpenedText
import com.example.apptracker.ui.routes.apps.AppListItem
import com.example.apptracker.ui.routes.apps.AppsScreenApp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatsPage(
    navController: NavController,
    viewModel: StatsViewModel
) {
    val screenState by viewModel.state.collectAsState()
    val apps by screenState.apps.collectAsState(initial = listOf())

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.stats_nav_bar_button_name)) }
            )
        }
    ) { padding ->
        Divider()
        LazyColumn(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                //.systemBarsPadding()
                .fillMaxSize()
        ) {
            items(apps) {
                StatsCard(
                    app = it,
                    onClick = {

                    }
                )
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