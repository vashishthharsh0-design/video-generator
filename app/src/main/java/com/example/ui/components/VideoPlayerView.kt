package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.AspectRatioSetting
import com.example.models.CameraMotion
import com.example.models.VideoClip
import com.example.models.VideoProject
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaRecordRed

@Composable
fun VideoPlayerView(
    project: VideoProject,
    currentPlayheadMs: Long,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onStepBack: () -> Unit,
    onStepForward: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine active clip at playhead
    var accumulatedMs = 0L
    var activeClip: VideoClip? = null
    var activeClipLocalMs = 0L

    for (clip in project.clips) {
        val clipDurationTimeline = (clip.durationMs / clip.speedMultiplier).toLong()
        if (currentPlayheadMs in accumulatedMs until (accumulatedMs + clipDurationTimeline)) {
            activeClip = clip
            activeClipLocalMs = ((currentPlayheadMs - accumulatedMs) * clip.speedMultiplier).toLong()
            break
        }
        accumulatedMs += clipDurationTimeline
    }
    if (activeClip == null && project.clips.isNotEmpty()) {
        activeClip = project.clips.last()
        activeClipLocalMs = activeClip.durationMs
    }

    // Active subtitle
    val activeSubtitle = project.subtitleTracks.firstOrNull {
        currentPlayheadMs in it.startMs..it.endMs
    }

    // Dynamic animation parameters for camera movement simulation
    val infiniteTransition = rememberInfiniteTransition(label = "CameraDrift")
    val driftAnim by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Drift"
    )

    val flarePulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Flare"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF07080C))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Monitor Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF000000))
                .border(1.dp, Color(0xFF232738), RoundedCornerShape(12.dp))
                .testTag("video_monitor_container"),
            contentAlignment = Alignment.Center
        ) {
            // Apply Project Aspect Ratio
            val ratio = project.aspectRatio.ratioWidth / project.aspectRatio.ratioHeight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                // If source photo is animated via Veo, render image frame
                if (activeClip?.sourceImageUri != null) {
                    AsyncImage(
                        model = activeClip.sourceImageUri,
                        contentDescription = "Veo Animated Frame",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val scale = 1.06f + (driftAnim / 15f) * 0.04f
                                scaleX = scale
                                scaleY = scale
                                translationX = driftAnim * 2.5f
                            }
                    )
                }

                // Generative Dynamic Scene Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("generative_scene_canvas")
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val color1 = Color(activeClip?.primaryColor ?: 0xFF00E5FF)
                    val color2 = Color(activeClip?.secondaryColor ?: 0xFF7C4DFF)

                    val motion = activeClip?.cameraMotion ?: CameraMotion.DOLLY_IN
                    val offsetX = when (motion) {
                        CameraMotion.PAN_RIGHT -> driftAnim * 2
                        CameraMotion.ORBIT_360 -> driftAnim * 1.5f
                        else -> 0f
                    }
                    val offsetY = when (motion) {
                        CameraMotion.CRANE_UP -> -driftAnim * 1.5f
                        else -> 0f
                    }

                    // Background Atmospheric Gradient
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color1.copy(alpha = 0.45f),
                                color2.copy(alpha = 0.25f),
                                Color(0xFF0B0D14)
                            ),
                            center = Offset(canvasWidth / 2 + offsetX, canvasHeight / 2 + offsetY),
                            radius = canvasWidth * 0.85f
                        )
                    )

                    // Cinematic perspective grid (horizon wireframe)
                    val horizonY = canvasHeight * 0.58f + offsetY * 0.5f
                    for (i in -4..4) {
                        val startX = (canvasWidth / 2) + (i * canvasWidth * 0.28f) + offsetX
                        drawLine(
                            color = color1.copy(alpha = 0.18f),
                            start = Offset(startX, canvasHeight),
                            end = Offset(canvasWidth / 2 + offsetX * 0.2f, horizonY),
                            strokeWidth = 1.2f
                        )
                    }

                    // Horizon line
                    drawLine(
                        color = color2.copy(alpha = 0.35f),
                        start = Offset(0f, horizonY),
                        end = Offset(canvasWidth, horizonY),
                        strokeWidth = 1.5f
                    )

                    // Cinematic stylized sun / aperture disc
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color1.copy(alpha = 0.8f), color2.copy(alpha = 0.1f), Color.Transparent),
                            center = Offset(canvasWidth / 2 + offsetX * 0.4f, horizonY - 40f + offsetY),
                            radius = 90f
                        ),
                        radius = 90f,
                        center = Offset(canvasWidth / 2 + offsetX * 0.4f, horizonY - 40f + offsetY)
                    )

                    // Anamorphic Lens Flare horizontal streak
                    val flareY = horizonY - 40f + offsetY
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                CinemaPrimaryCyan.copy(alpha = 0.3f * flarePulse),
                                Color.White.copy(alpha = 0.9f * flarePulse),
                                CinemaAccentMagenta.copy(alpha = 0.4f * flarePulse),
                                Color.Transparent
                            )
                        ),
                        start = Offset(0f, flareY),
                        end = Offset(canvasWidth, flareY),
                        strokeWidth = 2.5f
                    )

                    // Apply Color Grading LUT Tint if active
                    val lut = activeClip?.lutFilter
                    if (lut != null && lut.tintColor != 0x00000000L) {
                        drawRect(
                            color = Color(lut.tintColor),
                            size = Size(canvasWidth, canvasHeight)
                        )
                    }

                    // Subtle Vignette border
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0x99000000)),
                            center = Offset(canvasWidth / 2, canvasHeight / 2),
                            radius = canvasWidth * 0.7f
                        )
                    )
                }

                // Subtitle Overlay
                if (activeSubtitle != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp, start = 24.dp, end = 24.dp)
                            .background(Color(0xCC000000), RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = activeSubtitle.text,
                            color = Color(0xFFFFEB3B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                // Top HUD: Resolution & Live Timecode
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Recording / Playback tally
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xAA111422), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) CinemaRecordRed else Color(0xFF6B7280))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isPlaying) "PLAY" else "STBY",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = project.resolution.badge,
                            color = CinemaPrimaryCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Current Timecode
                    val timecodeStr = formatTimecode(currentPlayheadMs, project.framerate)
                    Text(
                        text = timecodeStr,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(Color(0xAA000000), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Active Scene Pill at Bottom Left
                if (activeClip != null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color(0xAA10121C), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeClip.title,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${activeClip.speedMultiplier}x",
                            color = CinemaPrimaryCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Playback Transport Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onStepBack,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("step_backward_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Step Back",
                    tint = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                onClick = onTogglePlay,
                shape = CircleShape,
                color = CinemaPrimaryCyan,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("play_pause_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF090A0F),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = onStepForward,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("step_forward_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Step Forward",
                    tint = Color(0xFF94A3B8)
                )
            }
        }
    }
}

fun formatTimecode(ms: Long, fps: Int): String {
    val totalSeconds = ms / 1000
    val frames = ((ms % 1000) * fps / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", minutes, seconds, frames)
}
