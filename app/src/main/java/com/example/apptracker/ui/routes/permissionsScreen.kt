package com.example.apptracker.ui.routes

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat.startActivity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.util.navigation.Route
import com.example.apptracker.util.permissions.isPackageUsagePermissionAccessGranted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionPage(
    navController: NavController,
    context: Context
) {

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()
    val state = lifecycleState.value

    val packageUsagePermissionGranted = isPackageUsagePermissionAccessGranted(context)

    if (packageUsagePermissionGranted) {
        navController.navigate(Route.Apps.path) {
            launchSingleTop = true
            restoreState = true
        }
    } else {
        //BackHandler() { /* consume back button input */ }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { ResourceText(R.string.permission_package_usage_title) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(10.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                PermissionCard(
                    text = R.string.permission_package_usage_description,
                    button = R.string.permission_package_usage_grant_button
                ) {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    text: Int,
    button: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            ResourceText(text)
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onClick
                ) {
                    ResourceText(button)
                }
            }
        }
    }
}

@Composable
private fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}