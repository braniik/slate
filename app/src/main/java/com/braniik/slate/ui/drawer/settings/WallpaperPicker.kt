package com.braniik.slate.ui.drawer.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braniik.slate.data.WallpaperConfig
import com.braniik.slate.data.extractWallpaperColors
import com.braniik.slate.data.loadWallpaperBitmap
import com.braniik.slate.data.saveWallpaperImage
import com.braniik.slate.ui.theme.SlateBackground
import com.braniik.slate.ui.theme.SlateOnBackground
import com.braniik.slate.ui.theme.SlateScrim
import com.braniik.slate.ui.theme.SlateSubtle
import com.braniik.slate.ui.theme.SlateSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private data class DirectionOption(val key: String, val symbol: String)

private val directionGrid = listOf(
    DirectionOption("top_left_to_bottom_right", "↘"), DirectionOption("top_to_bottom", "↓"), DirectionOption("top_right_to_bottom_left", "↙"),
    DirectionOption("left_to_right", "→"), DirectionOption("", ""), DirectionOption("right_to_left", "←"),
    DirectionOption("bottom_left_to_top_right", "↗"), DirectionOption("bottom_to_top", "↑"), DirectionOption("bottom_right_to_top_left", "↖")
)

@Composable
fun WallpaperPicker(
    current: WallpaperConfig,
    onSave: (WallpaperConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(current.mode) }
    var solidColor by remember { mutableIntStateOf(current.solidColor) }
    var gradientStart by remember { mutableIntStateOf(current.gradientStart) }
    var gradientEnd by remember { mutableIntStateOf(current.gradientEnd) }
    var gradientDirection by remember { mutableStateOf(current.gradientDirection) }
    var editingSlot by remember { mutableStateOf("start") }

    var imagePath by remember { mutableStateOf(current.imagePath) }
    var imageDominantColor by remember { mutableIntStateOf(current.imageDominantColor) }
    var previewBitmap by remember {
        mutableStateOf(
            if (current.mode == "image" && current.imagePath.isNotBlank())
                loadWallpaperBitmap(current.imagePath)
            else null
        )
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                val path = saveWallpaperImage(context, it) ?: return@launch
                val colors = extractWallpaperColors(path)
                val bmp = loadWallpaperBitmap(path)
                imagePath = path
                imageDominantColor = colors.dominant
                previewBitmap = bmp
            }
        }
    }

    val activeColor = when {
        mode == "solid" -> solidColor
        mode == "gradient" && editingSlot == "start" -> gradientStart
        mode == "gradient" -> gradientEnd
        else -> 0
    }

    fun setActiveColor(argb: Int) {
        when {
            mode == "solid" -> solidColor = argb
            mode == "gradient" && editingSlot == "start" -> gradientStart = argb
            mode == "gradient" -> gradientEnd = argb
        }
    }

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
                .width(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SlateSurface)
                .clickable(indication = null, interactionSource = cardInteraction) {}
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "wallpaper",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = SlateOnBackground
            )

            Spacer(Modifier.height(4.dp))

            WallpaperPreview(
                mode = mode,
                solidColor = solidColor,
                gradientStart = gradientStart,
                gradientEnd = gradientEnd,
                gradientDirection = gradientDirection,
                imageBitmap = previewBitmap
            )

            Spacer(Modifier.height(8.dp))

            SectionLabel("mode")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip("solid", mode == "solid") { mode = "solid" }
                ModeChip("gradient", mode == "gradient") { mode = "gradient" }
                ModeChip("image", mode == "image") { mode = "image" }
            }

            if (mode == "gradient") {
                Spacer(Modifier.height(8.dp))
                SectionLabel("editing")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColorSlotChip("start", Color(gradientStart), editingSlot == "start") {
                        editingSlot = "start"
                    }
                    ColorSlotChip("end", Color(gradientEnd), editingSlot == "end") {
                        editingSlot = "end"
                    }
                }

                Spacer(Modifier.height(8.dp))
                SectionLabel("direction")
                DirectionGrid(gradientDirection) { gradientDirection = it }
            }

            if (mode == "image") {
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateSubtle.copy(alpha = 0.3f))
                        .clickable { imagePicker.launch("image/*") }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (imagePath.isBlank()) "pick image" else "change image",
                        fontSize = 13.sp,
                        color = SlateOnBackground,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (mode != "image") {
                Spacer(Modifier.height(8.dp))

                SectionLabel("hex")
                HexInput(activeColor) { setActiveColor(it) }

                Spacer(Modifier.height(8.dp))
                SectionLabel("rgb")
                RgbSliders(activeColor) { setActiveColor(it) }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateOnBackground)
                    .clickable {
                        onSave(
                            WallpaperConfig(
                                mode = mode,
                                solidColor = solidColor,
                                gradientStart = gradientStart,
                                gradientEnd = gradientEnd,
                                gradientDirection = gradientDirection,
                                imagePath = imagePath,
                                imageDominantColor = imageDominantColor
                            )
                        )
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("save", fontSize = 13.sp, color = SlateBackground, letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
private fun WallpaperPreview(
    mode: String,
    solidColor: Int,
    gradientStart: Int,
    gradientEnd: Int,
    gradientDirection: String,
    imageBitmap: ImageBitmap?
) {
    when {
        mode == "image" && imageBitmap != null -> {
            Image(
                bitmap = imageBitmap,
                contentDescription = "wallpaper preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        mode == "image" -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateBackground)
                    .border(1.dp, SlateSubtle, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("no image selected", fontSize = 11.sp, color = SlateSubtle)
            }
        }
        else -> {
            val brush = when (mode) {
                "gradient" -> when (gradientDirection) {
                    "top_to_bottom", "top_left_to_bottom_right", "top_right_to_bottom_left" ->
                        Brush.verticalGradient(listOf(Color(gradientStart), Color(gradientEnd)))
                    "bottom_to_top", "bottom_left_to_top_right", "bottom_right_to_top_left" ->
                        Brush.verticalGradient(listOf(Color(gradientEnd), Color(gradientStart)))
                    "left_to_right" ->
                        Brush.horizontalGradient(listOf(Color(gradientStart), Color(gradientEnd)))
                    "right_to_left" ->
                        Brush.horizontalGradient(listOf(Color(gradientEnd), Color(gradientStart)))
                    else -> Brush.verticalGradient(listOf(Color(gradientStart), Color(gradientEnd)))
                }
                else -> SolidColor(Color(solidColor))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush)
                    .border(1.dp, SlateSubtle, RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun ModeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) SlateOnBackground else Color.Transparent
    val fg = if (selected) SlateBackground else SlateSubtle
    val border = if (selected) SlateOnBackground else SlateSubtle

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, fontSize = 12.sp, color = fg, letterSpacing = 1.sp)
    }
}

@Composable
private fun ColorSlotChip(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    val border = if (selected) SlateOnBackground else SlateSubtle
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, SlateSubtle, CircleShape)
        )
        Text(
            label,
            fontSize = 12.sp,
            color = if (selected) SlateOnBackground else SlateSubtle,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun DirectionGrid(current: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (row in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (col in 0..2) {
                    val opt = directionGrid[row * 3 + col]
                    if (opt.key.isEmpty()) {
                        Box(Modifier.size(36.dp))
                    } else {
                        val selected = current == opt.key
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selected) SlateOnBackground else Color.Transparent)
                                .border(1.dp, if (selected) SlateOnBackground else SlateSubtle, RoundedCornerShape(6.dp))
                                .clickable { onSelect(opt.key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                opt.symbol,
                                fontSize = 16.sp,
                                color = if (selected) SlateBackground else SlateSubtle
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HexInput(argb: Int, onColorChanged: (Int) -> Unit) {
    val hex = "%06X".format(argb and 0xFFFFFF)
    var text by remember(argb) { mutableStateOf(hex) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("#", fontSize = 14.sp, color = SlateSubtle)
        BasicTextField(
            value = text,
            onValueChange = { raw ->
                val filtered = raw.filter { it in "0123456789ABCDEFabcdef" }.take(6)
                text = filtered
                if (filtered.length == 6) {
                    try {
                        val parsed = filtered.toLong(16).toInt() or 0xFF000000.toInt()
                        onColorChanged(parsed)
                    } catch (_: Exception) {}
                }
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = SlateOnBackground,
                letterSpacing = 2.sp
            ),
            cursorBrush = SolidColor(SlateOnBackground),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SlateBackground)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun RgbSliders(argb: Int, onColorChanged: (Int) -> Unit) {
    val color = Color(argb)
    val r = (color.red * 255).toInt()
    val g = (color.green * 255).toInt()
    val b = (color.blue * 255).toInt()

    fun rebuild(red: Int, green: Int, blue: Int): Int =
        Color(red / 255f, green / 255f, blue / 255f).toArgb()

    RgbRow("r", r, Color(1f, 0f, 0f)) { onColorChanged(rebuild(it, g, b)) }
    RgbRow("g", g, Color(0f, 0.7f, 0f)) { onColorChanged(rebuild(r, it, b)) }
    RgbRow("b", b, Color(0.2f, 0.4f, 1f)) { onColorChanged(rebuild(r, g, it)) }
}

@Composable
private fun RgbRow(label: String, value: Int, accent: Color, onChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, fontSize = 11.sp, color = SlateSubtle, modifier = Modifier.width(12.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = SlateSubtle
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            "%d".format(value),
            fontSize = 11.sp,
            color = SlateSubtle,
            modifier = Modifier.width(28.dp)
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 11.sp, color = SlateSubtle, letterSpacing = 2.sp)
}