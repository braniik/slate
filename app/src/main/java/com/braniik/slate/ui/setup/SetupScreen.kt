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

private enum class SetupStep { LAYOUT, ORIENTATION }

@Composable
fun SetupScreen(onFinish: (LauncherSettings) -> Unit) {
    var step by remember { mutableStateOf(SetupStep.LAYOUT) }
    var layoutMode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        when (step) {
            SetupStep.LAYOUT -> LayoutStep(onSelect = { mode ->
                layoutMode = mode
                if (mode == "list") {
                    step = SetupStep.ORIENTATION
                } else {
                    onFinish(LauncherSettings(layoutMode = mode))
                }
            })

            SetupStep.ORIENTATION -> OrientationStep(onSelect = { orientation ->
                onFinish(LauncherSettings(
                    layoutMode = layoutMode,
                    listOrientation = orientation
                ))
            })
        }
    }
}

@Composable
private fun LayoutStep(onSelect: (String) -> Unit) {
    var picked by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        SetupHeader(
            title = "slate",
            subtitle = "how do you like your apps displayed?"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            SetupCard(
                label = "freescreen",
                description = "place anywhere",
                selected = picked == "freescreen",
                onClick = {
                    picked = "freescreen"
                    onSelect("freescreen")
                }
            )
            SetupCard(
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
private fun OrientationStep(onSelect: (String) -> Unit) {
    var picked by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        SetupHeader(
            title = "list",
            subtitle = "which direction?"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            SetupCard(
                label = "vertical",
                description = "scroll up & down",
                selected = picked == "vertical",
                onClick = {
                    picked = "vertical"
                    onSelect("vertical")
                }
            )
            SetupCard(
                label = "horizontal",
                description = "scroll left & right",
                selected = picked == "horizontal",
                onClick = {
                    picked = "horizontal"
                    onSelect("horizontal")
                }
            )
        }
    }
}

@Composable
private fun SetupHeader(title: String, subtitle: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            title,
            fontSize = 36.sp,
            fontWeight = FontWeight.Light,
            color = White,
            letterSpacing = 8.sp
        )
        Text(subtitle, fontSize = 14.sp, color = Subtle)
    }
}

@Composable
private fun SetupCard(
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