package com.braniik.slate.ui.setup

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.LauncherSettings

private val White = Color(0xFFFFFFFF)
private val Subtle = Color(0xFF444444)

@Composable
fun SetupScreen(onFinish: (LauncherSettings) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        LayoutStep(onSelect = { mode ->
            onFinish(LauncherSettings(layoutMode = mode))
        })
    }
}

@Composable
private fun LayoutStep(onSelect: (String) -> Unit) {
    var picked by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "slate",
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                color = White,
                letterSpacing = 8.sp
            )
            Text(
                "how do you like your apps displayed?",
                fontSize = 14.sp,
                color = Subtle
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            LayoutCard(
                label = "freescreen",
                description = "place anywhere",
                selected = picked == "freescreen",
                onClick = {
                    picked = "freescreen"
                    onSelect("freescreen")
                }
            )
            LayoutCard(
                label = "list",
                description = "ordered rows",
                selected = picked == "list",
                onClick = {
                    picked = "list"
                    onSelect("list")
                }
            )
        }
    }
}

@Composable
private fun LayoutCard(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) White else Subtle
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            label,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) White else Subtle
        )
        Text(description, fontSize = 11.sp, color = Subtle)
    }
}