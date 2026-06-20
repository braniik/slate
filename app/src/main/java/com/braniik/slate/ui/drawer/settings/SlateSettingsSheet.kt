package com.braniik.slate.ui.drawer.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.WallpaperConfig
import com.braniik.slate.data.discoverIconPacks
import com.braniik.slate.data.loadWallpaperBitmap
import com.braniik.slate.ui.theme.SlateDanger
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateScrim
import com.braniik.slate.ui.theme.SlateSubtle
import com.braniik.slate.ui.theme.SlateSurface

@Composable
fun SlateSettingsSheet(
    layoutMode: String,
    listOrientation: String,
    toolbarPosition: String,
    toolbarTitle: String,
    wallpaperConfig: WallpaperConfig,
    selectedIconPack: String,
    onSwitchMode: (String) -> Unit,
    onListOrientationChange: (String) -> Unit,
    onToolbarPositionChange: (String) -> Unit,
    onToolbarTitleChange: (String) -> Unit,
    onIconPackChange: (String) -> Unit,
    onOpenWallpaperPicker: () -> Unit,
    onClose: () -> Unit
) {
    var confirmSwitch by remember { mutableStateOf(false) }
    val targetMode = if (layoutMode == "freescreen") "list" else "freescreen"

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("settings", fontSize = 14.sp, color = SlateSubtle, letterSpacing = 2.sp)
                IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Close, "close", tint = SlateSubtle, modifier = Modifier.size(18.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SectionLabel("layout")

                Text(
                    layoutMode,
                    fontSize = 14.sp,
                    color = SlateOnBackground,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateSubtle.copy(alpha = 0.3f))
                        .clickable { confirmSwitch = true }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "change to $targetMode",
                        fontSize = 13.sp,
                        color = SlateOnBackground,
                        letterSpacing = 1.sp
                    )
                }

                if (layoutMode == "list") {
                    Spacer(Modifier.height(24.dp))
                    SectionLabel("list orientation")
                    OrientationOption("vertical", "scroll up & down", listOrientation == "vertical") {
                        onListOrientationChange("vertical")
                    }
                    OrientationOption("horizontal", "scroll left & right", listOrientation == "horizontal") {
                        onListOrientationChange("horizontal")
                    }
                }

                Spacer(Modifier.height(24.dp))
                SectionLabel("toolbar position")
                OrientationOption("top", "default", toolbarPosition == "top") {
                    onToolbarPositionChange("top")
                }
                OrientationOption("bottom", "near your thumb", toolbarPosition == "bottom") {
                    onToolbarPositionChange("bottom")
                }
                OrientationOption("left", "side strip", toolbarPosition == "left") {
                    onToolbarPositionChange("left")
                }
                OrientationOption("right", "side strip", toolbarPosition == "right") {
                    onToolbarPositionChange("right")
                }

                Spacer(Modifier.height(24.dp))
                SectionLabel("title")
                Text(
                    "shown in the toolbar",
                    fontSize = 11.sp,
                    color = SlateSubtle,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
                TitleField(toolbarTitle, onToolbarTitleChange)

                Spacer(Modifier.height(24.dp))
                SectionLabel("wallpaper")

                WallpaperPreviewRow(wallpaperConfig)

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateSubtle.copy(alpha = 0.3f))
                        .clickable { onOpenWallpaperPicker() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "customize",
                        fontSize = 13.sp,
                        color = SlateOnBackground,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(24.dp))
                SectionLabel("icon pack")

                val context = LocalContext.current
                val iconPacks = remember { discoverIconPacks(context) }

                OrientationOption(
                    "none",
                    "system default",
                    selectedIconPack.isBlank()
                ) { onIconPackChange("") }

                iconPacks.forEach { pack ->
                    OrientationOption(
                        pack.label,
                        pack.packageName,
                        selectedIconPack == pack.packageName
                    ) { onIconPackChange(pack.packageName) }
                }

                if (iconPacks.isEmpty()) {
                    Text(
                        "no icon packs installed",
                        fontSize = 11.sp,
                        color = SlateSubtle,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }
        }

        if (confirmSwitch) {
            SwitchConfirmDialog(
                targetMode = targetMode,
                onConfirm = {
                    confirmSwitch = false
                    onSwitchMode(targetMode)
                },
                onDismiss = { confirmSwitch = false }
            )
        }
    }
}

@Composable
private fun SwitchConfirmDialog(
    targetMode: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "switch to $targetMode?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = SlateOnBackground
            )

            Text(
                "your current layout will be reset. apps stay, but their arrangement starts fresh.",
                fontSize = 12.sp,
                color = SlateSubtle,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateDanger)
                    .clickable { onConfirm() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("switch", fontSize = 13.sp, color = SlateOnBackground, letterSpacing = 2.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDismiss() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("cancel", fontSize = 13.sp, color = SlateSubtle, letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
private fun TitleField(value: String, onValueChange: (String) -> Unit) {
    var text by remember { mutableStateOf(value) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateSubtle.copy(alpha = 0.3f))
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = { new ->
                text = new
                onValueChange(new)
            },
            singleLine = true,
            textStyle = TextStyle(color = SlateOnBackground, fontSize = 14.sp, letterSpacing = 1.sp),
            cursorBrush = SolidColor(SlateOnBackground),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (text.isEmpty()) {
                    Text(
                        "hidden",
                        fontSize = 14.sp,
                        color = SlateSubtle,
                        letterSpacing = 1.sp
                    )
                }
                inner()
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        color = SlateSubtle,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun OrientationOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (selected) SlateOnBackground else SlateSubtle)
        )
        Column(
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(label, fontSize = 14.sp, color = if (selected) SlateOnBackground else SlateSubtle)
            Text(description, fontSize = 11.sp, color = SlateSubtle)
        }
    }
}

@Composable
private fun WallpaperPreviewRow(config: WallpaperConfig) {
    if (config.mode == "image" && config.imagePath.isNotBlank()) {
        val bitmap = remember(config.imagePath) { loadWallpaperBitmap(config.imagePath) }
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            return
        }
    }

    val brush = when (config.mode) {
        "gradient" -> Brush.horizontalGradient(
            listOf(Color(config.gradientStart), Color(config.gradientEnd))
        )
        else -> SolidColor(Color(config.solidColor))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
            .border(1.dp, SlateSubtle, RoundedCornerShape(8.dp))
    )
}