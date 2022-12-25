package com.example.apptracker.ui.routes.settings

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
import com.example.apptracker.util.navigation.SettingsListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    navController: NavController
) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription = stringResource( id = R.string.back_button_description ))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val listItems = listOf(
                    SettingsListItem.Appearance
                )
                items(listItems) { item ->
                    SettingsListEntry(
                        navController = navController,
                        listItem = item
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsListEntry(
    navController: NavController,
    listItem: SettingsListItem
) {
    Card(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        onClick = {
            navController.navigate(listItem.route) {
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
            headlineText = { Text(stringResource(id = headline)) },
            supportingText = {Text(stringResource(id = listItem.supporting))},
            leadingContent = { Icon(painter = painterResource(id = listItem.icon), contentDescription = stringResource(headline)) },
        )
    }
}