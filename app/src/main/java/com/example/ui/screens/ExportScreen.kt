package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.AspectRatioSetting
import com.example.models.ExportResolution
import com.example.models.VideoCodec
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaRecordRed
import com.example.ui.theme.CinemaSecondaryPurple
import com.example.ui.theme.CinemaSuccessGreen
import com.example.viewmodel.CineFlowViewModel

@Composable
fun ExportScreen(
    viewModel: CineFlowViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val resolution by viewModel.exportResolution.collectAsState()
    val aspectRatio by viewModel.exportAspectRatio.collectAsState()
    val codec by viewModel.exportCodec.collectAsState()
    val fps by viewModel.exportFps.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val exportProgress by viewModel.exportProgress.collectAsState()
    val exportStage by viewModel.exportStage.collectAsState()
    val exportedPath by viewModel.exportedFilePath.collectAsState()

    val context = LocalContext.current
    val durationSeconds = (viewModel.getTotalDurationMs() / 1000f).coerceAtLeast(1f)
    val estimatedMb = when (resolution) {
        ExportResolution.RES_4K -> (durationSeconds * 6.5f).toInt()
        ExportResolution.RES_1080P -> (durationSeconds * 2.8f).toInt()
        ExportResolution.RES_720P -> (durationSeconds * 1.2f).toInt()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .padding(16.dp)
            .testTag("export_screen")
    ) {
        item {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF003840),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.IosShare, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Export & Delivery Studio",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Render master file in 4K UHD with tailored aspect ratios",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            // Platform Quick Presets
            Text("One-Tap Platform Presets", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlatformPresetCard(
                    title = "YouTube 4K",
                    subtitle = "16:9 • 4K • 24fps",
                    isSelected = resolution == ExportResolution.RES_4K && aspectRatio == AspectRatioSetting.RATIO_16_9,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.exportResolution.value = ExportResolution.RES_4K
                        viewModel.setProjectAspectRatio(AspectRatioSetting.RATIO_16_9)
                        viewModel.exportFps.value = 24
                        viewModel.exportCodec.value = VideoCodec.H264
                    }
                )

                PlatformPresetCard(
                    title = "TikTok / Reels",
                    subtitle = "9:16 • 1080p • 60fps",
                    isSelected = resolution == ExportResolution.RES_1080P && aspectRatio == AspectRatioSetting.RATIO_9_16,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.exportResolution.value = ExportResolution.RES_1080P
                        viewModel.setProjectAspectRatio(AspectRatioSetting.RATIO_9_16)
                        viewModel.exportFps.value = 60
                        viewModel.exportCodec.value = VideoCodec.HEVC
                    }
                )

                PlatformPresetCard(
                    title = "Cinema 21:9",
                    subtitle = "21:9 • 4K • ProRes",
                    isSelected = resolution == ExportResolution.RES_4K && aspectRatio == AspectRatioSetting.RATIO_21_9,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.exportResolution.value = ExportResolution.RES_4K
                        viewModel.setProjectAspectRatio(AspectRatioSetting.RATIO_21_9)
                        viewModel.exportFps.value = 24
                        viewModel.exportCodec.value = VideoCodec.PRORES
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Resolution Section
            Text("Resolution Master", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExportResolution.values().forEach { res ->
                    val isSelected = resolution == res
                    Card(
                        onClick = { viewModel.exportResolution.value = res },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF1B2238) else Color(0xFF131522)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) CinemaPrimaryCyan else Color(0xFF22263D)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("res_${res.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(res.label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (res == ExportResolution.RES_4K) {
                                        Text(
                                            "RECOMMENDED",
                                            color = CinemaPrimaryCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Color(0xFF003840), RoundedCornerShape(3.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    "${res.width} x ${res.height} pixels",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = CinemaPrimaryCyan,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF090A0F), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Aspect Ratio Section
            Text("Aspect Ratio Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AspectRatioSetting.values().forEach { aspect ->
                    val isSelected = aspectRatio == aspect
                    Card(
                        onClick = { viewModel.setProjectAspectRatio(aspect) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF1B2238) else Color(0xFF131522)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) CinemaAccentMagenta else Color(0xFF22263D)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aspect_${aspect.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(aspect.label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(aspect.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = CinemaAccentMagenta,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Codec & Framerate Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Codec Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Encoding Codec", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(codec.label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            VideoCodec.values().forEach { c ->
                                Surface(
                                    onClick = { viewModel.exportCodec.value = c },
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (codec == c) CinemaPrimaryCyan else Color(0xFF1E2235),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            c.container,
                                            color = if (codec == c) Color(0xFF090A0F) else Color(0xFFCBD5E1),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Framerate Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Cinematic Framerate", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${fps} FPS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(24, 30, 60).forEach { f ->
                                Surface(
                                    onClick = { viewModel.exportFps.value = f },
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (fps == f) CinemaSecondaryPurple else Color(0xFF1E2235),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            "$f",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // File Size & Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10121C)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2438)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Estimated File Size", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("~$estimatedMb MB", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Duration & Tracks", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("${durationSeconds.toInt()}s • ${project.clips.size} Clips • 4 Tracks", color = CinemaPrimaryCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Export Actions & In-Progress Pipeline
            if (isExporting) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151829)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CinemaPrimaryCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = CinemaPrimaryCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(exportStage, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { exportProgress },
                            color = CinemaPrimaryCyan,
                            trackColor = Color(0xFF242A42),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${(exportProgress * 100).toInt()}% Rendered",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else if (exportedPath != null) {
                // Completed Delivery Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF11221B)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CinemaSuccessGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = CinemaSuccessGreen,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF090A0F), modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Complete! 4K Master Ready", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Saved to: $exportedPath",
                            color = Color(0xFF86EFAC),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Check out my new video rendered in 4K UHD with CineFlow AI: ${project.title}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Video"))
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaSuccessGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF090A0F), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Video", color = Color(0xFF090A0F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.dismissExport() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Done", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.startExport() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimaryCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_export_button")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF090A0F), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Export ${resolution.label} Master (${aspectRatio.label})",
                        color = Color(0xFF090A0F),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PlatformPresetCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFF1F263E) else Color(0xFF131522),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) CinemaPrimaryCyan else Color(0xFF22263D)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 9.sp, maxLines = 1)
        }
    }
}
