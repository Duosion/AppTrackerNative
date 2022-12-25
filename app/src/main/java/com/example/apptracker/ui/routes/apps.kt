package com.example.apptracker.ui.routes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppsPage(
    navController: NavController
) {
    Scaffold(
        //modifier = Modifier.systemBarsPadding(),
        topBar = {
            TopAppBar(
                //modifier = Modifier.systemBarsPadding(),
                title = { Text(stringResource(id = R.string.apps_nav_bar_button_name)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            var selectedTab by remember { mutableStateOf(0) }
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = selectedTab
            ) {
                val tabs = listOf(
                    "Hello!",
                    "World!"
                )

                tabs.forEachIndexed { index, text ->
                    Tab(
                        text = { Text(text) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        }
                    )
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp),
            ) {
                items(count = 20) { count ->

                    Card(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height(70.dp)
                            .fillMaxWidth(),
                        onClick = {

                        }
                    ) {
                        ListItem(
                            modifier = Modifier.fillMaxSize(),
                            headlineText = { Text("App #${count + 1}") },
                            leadingContent = { },
                            colors = ListItemDefaults.colors(
                                containerColor = Color(0, 0, 0, 0)
                            )
                        )
                    }
                }
            }
        }
    }
}