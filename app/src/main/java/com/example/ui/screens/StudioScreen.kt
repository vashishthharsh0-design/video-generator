package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.AspectRatioSetting
import com.example.ui.components.ClipInspectorSheet
import com.example.ui.components.LiveVoiceDirectorSheet
import com.example.ui.components.MultiTrackTimeline
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaSuccessGreen
import com.example.viewmodel.CineFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: CineFlowViewModel,
    onNavigateToGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playheadMs by viewModel.playheadPositionMs.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val zoom by viewModel.timelineZoom.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var showInspector by remember { mutableStateOf(false) }
    var showAspectMenu by remember { mutableStateOf(false) }
    var showLiveDirector by remember { mutableStateOf(false) }

    val selectedClip = project.clips.firstOrNull { it.id == selectedClipId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .testTag("studio_screen")
    ) {
        // Top Studio Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Project Title & Cloud Sync Pill
            Column {
                Text(
                    text = project.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = if (isCloudSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                        contentDescription = "Cloud Sync",
                        tint = if (isCloudSyncing) CinemaPrimaryCyan else CinemaSuccessGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCloudSyncing) "Syncing to Cloud..." else project.cloudSyncTimestamp,
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }

            // Quick Aspect Ratio Switcher & Dark Mode Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Surface(
                        onClick = { showAspectMenu = true },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E2235),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                        modifier = Modifier.testTag("aspect_ratio_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AspectRatio, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = project.aspectRatio.label,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showAspectMenu,
                        onDismissRequest = { showAspectMenu = false },
                        modifier = Modifier.background(Color(0xFF191C2B))
                    ) {
                        AspectRatioSetting.values().forEach { aspect ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(aspect.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(aspect.description, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    viewModel.setProjectAspectRatio(aspect)
                                    showAspectMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    onClick = { showLiveDirector = true },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF2B1B3D),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CinemaAccentMagenta),
                    modifier = Modifier.testTag("live_director_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live AI",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { viewModel.toggleDarkMode() },
                    modifier = Modifier.size(34.dp).testTag("dark_mode_toggle")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = if (isDarkMode) CinemaPrimaryCyan else Color(0xFFFFB300),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Main Video Preview Monitor Canvas
        VideoPlayerView(
            project = project,
            currentPlayheadMs = playheadMs,
            isPlaying = isPlaying,
            onTogglePlay = { viewModel.togglePlayPause() },
            onStepBack = { viewModel.stepFrame(false) },
            onStepForward = { viewModel.stepFrame(true) }
        )

        // Interactive Multi-Track Timeline
        MultiTrackTimeline(
            project = project,
            currentPlayheadMs = playheadMs,
            totalDurationMs = viewModel.getTotalDurationMs(),
            selectedClipId = selectedClipId,
            zoom = zoom,
            onSeek = { viewModel.seekTo(it) },
            onSelectClip = { viewModel.selectClip(it) },
            onSplitClip = { viewModel.splitClipAtPlayhead() },
            onDuplicateClip = { selectedClipId?.let { viewModel.duplicateClip(it) } },
            onDeleteClip = { selectedClipId?.let { viewModel.deleteClip(it) } },
            onZoomChange = { viewModel.setZoom(it) },
            onOpenInspector = { showInspector = true },
            modifier = Modifier.weight(1f)
        )
    }

    // Modal Inspector Sheet
    if (showInspector && selectedClip != null) {
        ClipInspectorSheet(
            clip = selectedClip,
            onDismiss = { showInspector = false },
            onSpeedChange = { viewModel.updateClipSpeed(selectedClip.id, it) },
            onTransitionChange = { viewModel.updateClipTransition(selectedClip.id, it) },
            onLutChange = { viewModel.updateClipLut(selectedClip.id, it) },
            onDurationChange = { viewModel.updateClipDuration(selectedClip.id, it) }
        )
    }

    if (showLiveDirector) {
        LiveVoiceDirectorSheet(
            viewModel = viewModel,
            onDismiss = { showLiveDirector = false }
        )
    }
}
