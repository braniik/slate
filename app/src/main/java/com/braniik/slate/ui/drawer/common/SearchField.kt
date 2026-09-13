package com.braniik.slate.ui.drawer.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.LocalWallpaperTextColor

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "search"
) {
    val foreground = LocalWallpaperTextColor.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(foreground.copy(alpha = 0.10f))
            .padding(start = 12.dp)
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = foreground.copy(alpha = 0.55f),
            modifier = Modifier.size(16.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = foreground, fontSize = 14.sp),
            cursorBrush = SolidColor(foreground),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 14.dp),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) {
                        Text(placeholder, fontSize = 14.sp, color = foreground.copy(alpha = 0.45f))
                    }
                    inner()
                }
            }
        )

        if (value.isEmpty()) {
            Spacer(Modifier.size(12.dp))
        } else {
            IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(48.dp)) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "clear search",
                    tint = foreground.copy(alpha = 0.55f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}