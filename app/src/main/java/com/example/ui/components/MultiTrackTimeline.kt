package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.AudioTrackItem
import com.example.models.EffectItem
import com.example.models.SubtitleItem
import com.example.models.TransitionType
import com.example.models.VideoClip
import com.example.models.VideoProject
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaSecondaryPurple

@Composable
fun MultiTrackTimeline(
    project: VideoProject,
    currentPlayheadMs: Long,
    totalDurationMs: Long,
    selectedClipId: String?,
    zoom: Float,
    onSeek: (Long) -> Unit,
    onSelectClip: (String) -> Unit,
    onSplitClip: () -> Unit,
    onDuplicateClip: () -> Unit,
    onDeleteClip: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onOpenInspector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Timeline scale: 1 second = base 50dp * zoom
    val pxPerSecond = 55.dp * zoom
    val totalSeconds = (totalDurationMs / 1000f).coerceAtLeast(15f)
    val totalTimelineWidth = pxPerSecond * totalSeconds

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0C0E16))
            .border(1.dp, Color(0xFF1F2336), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(top = 10.dp)
            .testTag("multi_track_timeline")
    ) {
        // Quick Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Edit actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    onClick = onSplitClip,
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E2235),
                    modifier = Modifier.testTag("split_clip_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ContentCut, contentDescription = "Split", tint = CinemaPrimaryCyan, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Split", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    onClick = onDuplicateClip,
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E2235),
                    modifier = Modifier.testTag("duplicate_clip_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color(0xFFCBD5E1), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Duplicate", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (selectedClipId != null) {
                    Surface(
                        onClick = onOpenInspector,
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF282D46),
                        modifier = Modifier.testTag("clip_inspector_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Inspect", tint = CinemaAccentMagenta, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tune Clip", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDeleteClip,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_clip_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Zoom controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onZoomChange((zoom - 0.25f).coerceAtLeast(0.6f)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                }
                Text(
                    text = "${(zoom * 100).toInt()}%",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = { onZoomChange((zoom + 0.25f).coerceAtMost(2.5f)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                }
            }
        }

        // Timeline Track Layout: Fixed Track Headers on Left + Horizontally Scrollable Timeline Body on Right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            // Left Column: Track Labels
            Column(
                modifier = Modifier
                    .width(62.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF0F111B))
                    .border(1.dp, Color(0xFF1E2235))
            ) {
                // Header spacer for ruler
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .background(Color(0xFF0A0B12))
                        .padding(start = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("TRACK", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                // V1 Label
                TrackHeaderLabel("V1", Icons.Default.Movie, CinemaPrimaryCyan, 64.dp)
                // A1 Label
                TrackHeaderLabel("A1", Icons.Default.GraphicEq, CinemaAccentMagenta, 44.dp)
                // S1 Label
                TrackHeaderLabel("S1", Icons.Default.Subtitles, Color(0xFFFFD54F), 36.dp)
                // FX1 Label
                TrackHeaderLabel("FX1", Icons.Default.AutoAwesome, CinemaSecondaryPurple, 36.dp)
            }

            // Right Scrollable Timeline Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
            ) {
                // Main Timeline Body
                Column(
                    modifier = Modifier
                        .width(totalTimelineWidth)
                        .fillMaxHeight()
                ) {
                    // Time Ruler
                    TimecodeRuler(
                        totalSeconds = totalSeconds.toInt(),
                        pxPerSecond = pxPerSecond,
                        onSeek = onSeek
                    )

                    // Track 1: Video Track
                    VideoTrackRow(
                        project = project,
                        selectedClipId = selectedClipId,
                        pxPerSecond = pxPerSecond,
                        onSelectClip = onSelectClip
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Track 2: Audio Track
                    AudioTrackRow(
                        audioTracks = project.audioTracks,
                        pxPerSecond = pxPerSecond
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Track 3: Subtitles Track
                    SubtitleTrackRow(
                        subtitles = project.subtitleTracks,
                        pxPerSecond = pxPerSecond
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Track 4: Effects Track
                    EffectTrackRow(
                        effects = project.effectTracks,
                        pxPerSecond = pxPerSecond
                    )
                }

                // Draggable Playhead Needle
                val playheadOffset = (currentPlayheadMs / 1000f) * pxPerSecond.value
                Box(
                    modifier = Modifier
                        .offset { IntOffset(playheadOffset.toInt(), 0) }
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(CinemaPrimaryCyan)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaSeconds = dragAmount.x / (pxPerSecond.toPx() / 1f)
                                val deltaMs = (deltaSeconds * 1000).toLong()
                                onSeek(currentPlayheadMs + deltaMs)
                            }
                        }
                        .testTag("playhead_needle")
                ) {
                    // Playhead top marker diamond
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .offset((-4).dp, 0.dp)
                            .clip(CircleShape)
                            .background(CinemaPrimaryCyan)
                    )
                }
            }
        }
    }
}

@Composable
fun TrackHeaderLabel(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, height: androidx.compose.ui.unit.Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .border(0.5.dp, Color(0xFF1E2235))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = name, tint = tint, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(name, color = Color(0xFFE2E8F0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimecodeRuler(
    totalSeconds: Int,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    onSeek: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .background(Color(0xFF090A10))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedSec = offset.x / (pxPerSecond.toPx())
                    onSeek((clickedSec * 1000).toLong())
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pxSec = pxPerSecond.toPx()
            for (sec in 0..totalSeconds) {
                val x = sec * pxSec
                // Major tick mark
                drawLine(
                    color = Color(0xFF475569),
                    start = Offset(x, size.height - 12f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.5f
                )
                // Minor half-second tick
                drawLine(
                    color = Color(0xFF334155),
                    start = Offset(x + pxSec / 2f, size.height - 6f),
                    end = Offset(x + pxSec / 2f, size.height),
                    strokeWidth = 1f
                )
            }
        }

        // Second Labels
        Row {
            for (sec in 0..totalSeconds step 2) {
                Box(
                    modifier = Modifier
                        .width(pxPerSecond * 2)
                        .padding(start = 4.dp, top = 2.dp)
                ) {
                    Text(
                        text = "${sec}s",
                        color = Color(0xFF94A3B8),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun VideoTrackRow(
    project: VideoProject,
    selectedClipId: String?,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    onSelectClip: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF131522))
            .padding(vertical = 4.dp)
    ) {
        for (clip in project.clips) {
            val clipTimelineDurationMs = (clip.durationMs / clip.speedMultiplier).toLong()
            val clipWidth = pxPerSecond * (clipTimelineDurationMs / 1000f)

            val isSelected = clip.id == selectedClipId
            val borderColor = if (isSelected) CinemaPrimaryCyan else Color(0xFF262C44)
            val color1 = Color(clip.primaryColor)
            val color2 = Color(clip.secondaryColor)

            Box(
                modifier = Modifier
                    .width(clipWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 1.5.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(color1.copy(alpha = 0.85f), color2.copy(alpha = 0.85f))
                        )
                    )
                    .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(6.dp))
                    .clickable { onSelectClip(clip.id) }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = clip.title,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (clip.transitionIn != TransitionType.NONE) {
                            Text(
                                text = clip.transitionIn.displayName,
                                color = CinemaPrimaryCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0x99000000), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(clipTimelineDurationMs / 1000f)}s",
                            color = Color(0xFFE2E8F0),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${clip.speedMultiplier}x",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(Color(0x66000000), RoundedCornerShape(3.dp))
                                .padding(horizontal = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioTrackRow(
    audioTracks: List<AudioTrackItem>,
    pxPerSecond: androidx.compose.ui.unit.Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Color(0xFF10121C))
            .padding(vertical = 3.dp)
    ) {
        for (audio in audioTracks) {
            val audioWidth = pxPerSecond * (audio.durationMs / 1000f)
            Box(
                modifier = Modifier
                    .width(audioWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 1.5.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF221735))
                    .border(1.dp, Color(0xFF4C2B78), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = CinemaAccentMagenta, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(audio.title, color = Color(0xFFE2E8F0), fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                    }
                    if (audio.isNoiseReduced) {
                        Text(
                            text = "AI NR",
                            color = CinemaPrimaryCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color(0xFF003840), RoundedCornerShape(2.dp))
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubtitleTrackRow(
    subtitles: List<SubtitleItem>,
    pxPerSecond: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(Color(0xFF0E1019))
            .padding(vertical = 2.dp)
    ) {
        for (sub in subtitles) {
            val startX = pxPerSecond * (sub.startMs / 1000f)
            val width = pxPerSecond * ((sub.endMs - sub.startMs) / 1000f)

            Box(
                modifier = Modifier
                    .offset(x = startX)
                    .width(width)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2E2613))
                    .border(1.dp, Color(0xFF6B581B), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = sub.text,
                    color = Color(0xFFFFE082),
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun EffectTrackRow(
    effects: List<EffectItem>,
    pxPerSecond: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(Color(0xFF0C0D15))
            .padding(vertical = 2.dp)
    ) {
        for (fx in effects) {
            val startX = pxPerSecond * (fx.startMs / 1000f)
            val width = pxPerSecond * ((fx.endMs - fx.startMs) / 1000f)

            Box(
                modifier = Modifier
                    .offset(x = startX)
                    .width(width)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1B1833))
                    .border(1.dp, Color(0xFF3F376B), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = fx.name,
                    color = Color(0xFFD1C4E9),
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }
        }
    }
}
