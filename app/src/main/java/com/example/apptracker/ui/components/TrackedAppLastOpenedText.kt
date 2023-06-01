package com.example.apptracker.ui.components

import android.icu.text.RelativeDateTimeFormatter
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.apptracker.R
import com.example.apptracker.util.data.apps.TrackedApp
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

private const val SECONDS_IN_MINUTE = 60
private const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60 // 60 seconds in a minute
private const val SECONDS_IN_DAY = SECONDS_IN_HOUR * 24 // 24 hours in a day

@Composable
fun TrackedAppLastOpenedText(
    trackedApp: TrackedApp,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val formatter = RelativeDateTimeFormatter.getInstance()

    val dateNow = ZonedDateTime.now().toLocalDateTime()
    val openedTimestamp = trackedApp.openedTimestamp
    val lastOpenedDate = LocalDateTime.ofEpochSecond(openedTimestamp, 0, OffsetDateTime.now().offset)

    val secondsBetween = ChronoUnit.SECONDS.between(lastOpenedDate, dateNow)

    val openedToday = trackedApp.openedToday

    val lastDirection = RelativeDateTimeFormatter.Direction.LAST
    val prefix = if (openedToday) stringResource(id = R.string.apps_tracked_app_last_opened_opened_prefix) else stringResource(id = R.string.apps_tracked_app_last_opened_prefix)

    val relativeString = when {
        openedTimestamp == 0L -> {
            stringResource(id = R.string.apps_tracked_app_last_opened_default)
        }
        SECONDS_IN_MINUTE > secondsBetween -> {
            // "Opened now"
            "$prefix ${formatter.format(RelativeDateTimeFormatter.Direction.PLAIN, RelativeDateTimeFormatter.AbsoluteUnit.NOW)}"
        }
        SECONDS_IN_HOUR > secondsBetween -> {
            // "Opened 2 minutes ago"
            "$prefix ${formatter.format((secondsBetween / SECONDS_IN_MINUTE).toDouble(), lastDirection, RelativeDateTimeFormatter.RelativeUnit.MINUTES)}"
        }
        SECONDS_IN_DAY > secondsBetween -> {
            // "Opened 9 hours ago"
            "$prefix ${formatter.format((secondsBetween / SECONDS_IN_HOUR).toDouble(), lastDirection, RelativeDateTimeFormatter.RelativeUnit.HOURS)}"
        }
        (SECONDS_IN_DAY * 7) > secondsBetween -> {
            // "Opened 1 day ago"
            "$prefix ${formatter.format((secondsBetween / SECONDS_IN_DAY).toDouble(), lastDirection, RelativeDateTimeFormatter.RelativeUnit.DAYS)}"
        }
        else -> {
            // "Last opened 5/12/2023"
            "$prefix ${DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(lastOpenedDate)}"
        }
    }

    Text(
        text = relativeString,
        color = if (openedToday) MaterialTheme.colorScheme.primary else color,
        modifier = modifier,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}