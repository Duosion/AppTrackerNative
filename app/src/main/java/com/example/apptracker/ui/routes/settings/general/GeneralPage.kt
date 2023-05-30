package com.example.apptracker.ui.routes.settings.general

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.ui.routes.settings.SettingsListEntry
import com.example.apptracker.ui.routes.settings.SettingsListItemCard
import com.example.apptracker.util.navigation.SettingsListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralPage(
    navController: NavController
) {
    Scaffold (
        topBar = {
            BackTopAppBar(
                title = { Text(stringResource(id = R.string.settings_general_title)) },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    val context = LocalContext.current
                    SettingsListItemCard(
                        headlineContent = { ResourceText(id = R.string.settings_general_mangage_notifications_headline) },
                        onClick = {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                            ContextCompat.startActivity(context, intent, null)
                        }
                    )
                }
            }
        }
    }
}