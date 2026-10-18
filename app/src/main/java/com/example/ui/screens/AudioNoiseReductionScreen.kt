package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.LyriaMusicModel
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaRecordRed
import com.example.ui.theme.CinemaSecondaryPurple
import com.example.ui.theme.CinemaSuccessGreen
import com.example.viewmodel.CineFlowViewModel

enum class AudioStudioTab(val title: String) {
    NOISE_REDUCTION("Noise Reduction AI"),
    LYRIA_MUSIC("Lyria Music Studio")
}

data class PresetItem(
    val name: String,
    val db: Int,
    val hum: Boolean,
    val wind: Boolean,
    val dereverb: Boolean,
    val description: String
)

@Composable
fun AudioNoiseReductionScreen(
    viewModel: CineFlowViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val noiseConfig = project.noiseReduction
    val compareOriginal by viewModel.compareOriginalAudio.collectAsState()
    val spectrum by viewModel.spectralSpectrum.collectAsState()

    // Lyria music state
    val musicPrompt by viewModel.musicPrompt.collectAsState()
    val selectedLyriaModel by viewModel.selectedLyriaModel.collectAsState()
    val isGeneratingMusic by viewModel.isGeneratingMusic.collectAsState()

    var activeTab by remember { mutableStateOf(AudioStudioTab.NOISE_REDUCTION) }

    val presets = listOf(
        PresetItem("Neural Speech Isolation", -18, hum = true, wind = true, dereverb = true, "Removes background traffic & cafe noise while keeping vocal presence"),
        PresetItem("Wind & AC Hum Killer", -24, hum = true, wind = true, dereverb = false, "Deep notch filter targeting 50/60Hz electricity hum and microphone wind gusts"),
        PresetItem("De-Reverb / Echo Killer", -12, hum = false, wind = false, dereverb = true, "Tames harsh room acoustics, slap-back echo, and hollow studio reflections"),
        PresetItem("Studio Clean Room Master", -30, hum = true, wind = true, dereverb = true, "Extreme neural gate for podcast and dialogue broadcast perfection")
    )

    val musicSuggestions = listOf(
        "Blade Runner Cyberpunk Synthwave with analog pulse",
        "Hans Zimmer Dark Orchestral Strings & Braam horns",
        "Interstellar Atmospheric Space Drone & Pipe Organ",
        "Upbeat Electronic Lo-Fi B-roll Beat with warm Rhodes"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .padding(16.dp)
            .testTag("audio_noise_reduction_screen")
    ) {
        item {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF38194A),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = CinemaAccentMagenta, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Audio & Sound Studio",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AI background noise cleaner & Lyria soundtrack generator",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            // Segmented Tab Switcher: Noise Reduction vs Lyria Music
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141724))
                    .border(1.dp, Color(0xFF262C42), RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                AudioStudioTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    Surface(
                        onClick = { activeTab = tab },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CinemaAccentMagenta else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.title,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (activeTab == AudioStudioTab.LYRIA_MUSIC) {
            // ================= LYRIA MUSIC GENERATION =================
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF28243E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = CinemaAccentMagenta, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Music (Lyria AI)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Lyria Studio", color = CinemaPrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Model Selection (Lyria Clip vs Lyria Pro)
                        Text("Select Music Model", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LyriaMusicModel.values().forEach { model ->
                                val isSelected = selectedLyriaModel == model
                                Card(
                                    onClick = { viewModel.selectedLyriaModel.value = model },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF2B1D3D) else Color(0xFF161826)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) CinemaAccentMagenta else Color(0xFF262A3E)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(model.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            if (isSelected) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = CinemaAccentMagenta, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(model.description, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(model.modelId, color = CinemaPrimaryCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Music Description Prompt
                        Text("Soundtrack Prompt & Mood", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = musicPrompt,
                            onValueChange = { viewModel.musicPrompt.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .testTag("music_prompt_field"),
                            placeholder = { Text("e.g. Cinematic orchestral cyberpunk hybrid, heavy analog bassline, nostalgic 80s synth lead...", color = Color(0xFF64748B), fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CinemaAccentMagenta,
                                unfocusedBorderColor = Color(0xFF2D334D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color(0xFFCBD5E1)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Inspiration Chips
                        Text("Style suggestions:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(musicSuggestions) { sug ->
                                Surface(
                                    onClick = { viewModel.musicPrompt.value = sug },
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF1E2235),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B))
                                ) {
                                    Text(sug, color = Color(0xFFE2E8F0), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Generate Music Button
                        if (isGeneratingMusic) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF20162B), RoundedCornerShape(8.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = CinemaAccentMagenta, strokeWidth = 3.dp, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Composing with ${selectedLyriaModel.modelId}...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.generateMusicWithLyria() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaAccentMagenta),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_lyria_music_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Soundtrack & Add to Timeline", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timeline Audio Tracks List
                Text("Project Audio Tracks (${project.audioTracks.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    project.audioTracks.forEach { audio ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(audio.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${audio.type} • ${audio.durationMs / 1000}s duration", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                }
                                if (audio.lyriaModel != null) {
                                    Text(
                                        text = audio.lyriaModel,
                                        color = CinemaAccentMagenta,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier
                                            .background(Color(0xFF38152B), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // ================= NOISE REDUCTION AI =================
            item {
                // Spectrum Analyzer Canvas
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111422)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Equalizer, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Real-Time Spectral Analyzer",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val isEffectiveClean = noiseConfig.isEnabled && !compareOriginal
                            Text(
                                text = if (isEffectiveClean) "CLEANED AI MASTER" else "RAW NOISY INPUT",
                                color = if (isEffectiveClean) CinemaPrimaryCyan else CinemaRecordRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .background(
                                        if (isEffectiveClean) Color(0xFF003840) else Color(0xFF3D1414),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Spectrum Graphic Bars
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0A0B12))
                                .border(1.dp, Color(0xFF1E2235), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val barCount = spectrum.size
                                val barWidth = (size.width - (barCount - 1) * 8f) / barCount

                                for (i in 0 until barCount) {
                                    val barHeight = size.height * spectrum[i]
                                    val x = i * (barWidth + 8f)
                                    val y = size.height - barHeight

                                    val brush = Brush.verticalGradient(
                                        colors = if (!compareOriginal && noiseConfig.isEnabled) {
                                            listOf(CinemaPrimaryCyan, CinemaSecondaryPurple)
                                        } else {
                                            listOf(CinemaRecordRed, Color(0xFFFF9100))
                                        },
                                        startY = y,
                                        endY = size.height
                                    )

                                    drawRect(
                                        brush = brush,
                                        topLeft = Offset(x, y),
                                        size = Size(barWidth, barHeight)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Frequency Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("60Hz", "150Hz", "400Hz", "1kHz", "2.5kHz", "5kHz", "12kHz").forEach { freq ->
                                Text(freq, color = Color(0xFF64748B), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // A/B Comparison Toggle
                        Surface(
                            onClick = { viewModel.toggleAudioComparison(!compareOriginal) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (compareOriginal) Color(0xFF3D1E22) else Color(0xFF1D2235),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (compareOriginal) CinemaRecordRed else Color(0xFF2C334E)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("compare_audio_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Compare,
                                    contentDescription = null,
                                    tint = if (compareOriginal) CinemaRecordRed else CinemaPrimaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (compareOriginal) "Listening to: ORIGINAL RAW AUDIO (Noisy)" else "Tap to A/B Compare with Original Noisy Audio",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Master Toggle & Attenuation Slider
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Neural Noise Reduction", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Active on audio timeline master", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            Switch(
                                checked = noiseConfig.isEnabled,
                                onCheckedChange = { viewModel.toggleNoiseReduction(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF090A0F),
                                    checkedTrackColor = CinemaPrimaryCyan,
                                    uncheckedThumbColor = Color(0xFF64748B),
                                    uncheckedTrackColor = Color(0xFF1E2235)
                                ),
                                modifier = Modifier.testTag("noise_reduction_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Noise Attenuation Depth", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "${noiseConfig.reductionLevelDb} dB",
                                color = CinemaPrimaryCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Slider(
                            value = noiseConfig.reductionLevelDb.toFloat(),
                            onValueChange = { viewModel.setNoiseReductionDb(it.toInt()) },
                            valueRange = -36f..-6f,
                            steps = 29,
                            colors = SliderDefaults.colors(
                                thumbColor = CinemaPrimaryCyan,
                                activeTrackColor = CinemaPrimaryCyan,
                                inactiveTrackColor = Color(0xFF262C44)
                            ),
                            modifier = Modifier.testTag("attenuation_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gentle (-6dB)", color = Color(0xFF64748B), fontSize = 10.sp)
                            Text("Heavy Isolation (-36dB)", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                // AI Noise Reduction Presets
                Text("Filmmaker Presets", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { preset ->
                        val isSelected = noiseConfig.algorithmName == preset.name
                        Card(
                            onClick = {
                                viewModel.updateNoiseReductionPreset(
                                    preset.name,
                                    preset.db,
                                    preset.hum,
                                    preset.wind,
                                    preset.dereverb
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1B2035) else Color(0xFF131522)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) CinemaAccentMagenta else Color(0xFF22263D)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = preset.name,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${preset.db}dB",
                                            color = CinemaAccentMagenta,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier
                                                .background(Color(0xFF3B152A), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = preset.description,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
