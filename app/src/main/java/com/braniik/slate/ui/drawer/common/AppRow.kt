package com.braniik.slate.ui.drawer.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.LocalWallpaperTextColor
import com.braniik.slate.ui.drawer.AppInfo
import com.braniik.slate.ui.drawer.rememberAppIcon

private const val ROW_ICON_DP = 32

@Composable
internal fun AppRow(app: AppInfo, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Image(
            bitmap = rememberAppIcon(app, ROW_ICON_DP),
            contentDescription = app.label,
            modifier = Modifier
                .size(ROW_ICON_DP.dp)
                .clip(CircleShape)
        )
        Text(
            app.label,
            fontSize = 14.sp,
            color = LocalWallpaperTextColor.current,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}