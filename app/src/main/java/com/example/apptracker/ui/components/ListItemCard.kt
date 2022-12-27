package com.example.apptracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItemCard(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.shape,
    cardColors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    headlineText: @Composable () -> Unit,
    overlineText: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    listItemColors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = cardColors,
        elevation = elevation,
        border = border,
        onClick = onClick
    ) {
        ListItem(
            modifier = Modifier.fillMaxSize(),
            headlineText = headlineText,
            overlineText = overlineText,
            supportingText = supportingText,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            colors = listItemColors,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation
        )
    }
}