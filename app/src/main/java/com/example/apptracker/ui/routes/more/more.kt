package com.example.apptracker.ui.routes.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.util.navigation.MoreListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MorePage(
    navController: NavController
) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.more_nav_bar_button_name)) }
            )
        }
    ) { padding ->
        Divider()
        val listItems = listOf(
            MoreListItem.AddApps,
            MoreListItem.Categories,
            MoreListItem.Settings
        )
        LazyColumn(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                //.systemBarsPadding()
                .fillMaxSize()
        ) {
            items(listItems) {
                MostListEntry(
                    navController = navController,
                    listItem = it
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostListEntry(
    navController: NavController,
    listItem: MoreListItem
) {
    Card(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        onClick = {
            navController.navigate(listItem.route.path) {
                /*popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }*/
                launchSingleTop = true
                restoreState = true
            }
        }
    ) {
        val headline = listItem.headline
        ListItem(
            modifier = Modifier.fillMaxSize(),
            headlineContent = { Text(stringResource(id = headline)) },
            supportingContent = {Text(stringResource(id = listItem.supporting))},
            leadingContent = { Icon(
                painter = painterResource(id = listItem.icon),
                contentDescription = stringResource(headline),
                tint = MaterialTheme.colorScheme.primary
            )},
        )
    }
}