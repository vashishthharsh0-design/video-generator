package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.LutFilter
import com.example.models.TransitionType
import com.example.models.VideoClip
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaSecondaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipInspectorSheet(
    clip: VideoClip,
    onDismiss: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onTransitionChange: (TransitionType) -> Unit,
    onLutChange: (LutFilter) -> Unit,
    onDurationChange: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF131522),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("clip_inspector_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clip Inspector: ${clip.title}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Speed Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = CinemaAccentMagenta, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Playback Speed", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = "${clip.speedMultiplier}x",
                    color = CinemaPrimaryCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (spd in speedOptions) {
                    val isSelected = clip.speedMultiplier == spd
                    Surface(
                        onClick = { onSpeedChange(spd) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CinemaPrimaryCyan else Color(0xFF1E2235),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "${spd}x",
                                color = if (isSelected) Color(0xFF07080C) else Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Transition In
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = CinemaSecondaryPurple, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Transition In", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TransitionType.values()) { trans ->
                    val isSelected = clip.transitionIn == trans
                    Surface(
                        onClick = { onTransitionChange(trans) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CinemaSecondaryPurple else Color(0xFF1E2235),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CinemaPrimaryCyan else Color(0xFF2E334D))
                    ) {
                        Text(
                            text = trans.displayName,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cinematic LUT Color Grading
            Text("Cinematic LUT Color Grade", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(LutFilter.values()) { lut ->
                    val isSelected = clip.lutFilter == lut
                    Surface(
                        onClick = { onLutChange(lut) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF282D46) else Color(0xFF1E2235),
                        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) CinemaPrimaryCyan else Color(0xFF2E334D))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (lut.tintColor != 0L) Color(lut.tintColor) else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = lut.displayName,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Duration Trimmer Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clip Duration Trim", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = "${clip.durationMs / 1000f}s",
                    color = CinemaPrimaryCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Slider(
                value = (clip.durationMs / 1000f),
                onValueChange = { onDurationChange((it * 1000).toLong()) },
                valueRange = 1f..12f,
                steps = 21,
                colors = SliderDefaults.colors(
                    thumbColor = CinemaPrimaryCyan,
                    activeTrackColor = CinemaPrimaryCyan,
                    inactiveTrackColor = Color(0xFF242A3E)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
