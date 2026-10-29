package com.braniik.slate.ui.drawer

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.ui.drawer.common.AppRow
import com.braniik.slate.ui.drawer.common.SearchField

private const val MAX_RESULTS = 6

@Composable
internal fun LaunchSearch(
    apps: List<AppInfo>,
    onLaunch: (AppInfo) -> Unit,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val matches = remember(apps, query) { if (query.isBlank()) emptyList() else apps.matching(query) }
    val currentOnLaunch by rememberUpdatedState(onLaunch)
    val currentOnClose by rememberUpdatedState(onClose)

    val sole = soleMatch(query, matches)
    LaunchedEffect(sole?.key) {
        sole?.let { currentOnLaunch(it) }
    }

    val subtle = LocalWallpaperTextColor.current.copy(alpha = 0.55f)

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Bottom),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.ime))
            .pointerInput(Unit) { detectTapGestures { currentOnClose() } }
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        val hidden = matches.size - MAX_RESULTS
        if (hidden > 0) {
            Text(
                "$hidden more, keep typing",
                fontSize = 12.sp,
                color = subtle,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }

        matches.take(MAX_RESULTS).asReversed().forEach { app ->
            AppRow(app) { onLaunch(app) }
        }

        if (query.isNotBlank() && matches.isEmpty()) {
            Text(
                "nothing matches",
                fontSize = 14.sp,
                color = subtle,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp)
            )
        }

        SearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = "type to launch",
            autoFocus = true,
            onSearch = { matches.firstOrNull()?.let(onLaunch) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}