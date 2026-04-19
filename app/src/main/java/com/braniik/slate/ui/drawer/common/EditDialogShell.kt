package com.braniik.slate.ui.drawer.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.ui.theme.SlateBackground
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateScrim
import com.braniik.slate.ui.theme.SlateSubtle
import com.braniik.slate.ui.theme.SlateSurface

@Composable
fun EditDialogShell(
    title: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrimInteraction = remember { MutableInteractionSource() }
    val cardInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateScrim)
            .clickable(indication = null, interactionSource = scrimInteraction) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SlateSurface)
                .clickable(indication = null, interactionSource = cardInteraction) {}
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateOnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, "close", tint = SlateSubtle, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
            content()
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateOnBackground)
                    .clickable { onSave() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("save", fontSize = 13.sp, color = SlateBackground, letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
fun OptionLabel(text: String) {
    Text(text, fontSize = 12.sp, color = SlateSubtle, letterSpacing = 1.sp)
}