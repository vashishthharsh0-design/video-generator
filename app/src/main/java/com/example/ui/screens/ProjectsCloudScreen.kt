package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.VideoProject
import com.example.services.CloudSyncService
import com.example.ui.components.formatTimecode
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaSecondaryPurple
import com.example.ui.theme.CinemaSuccessGreen
import com.example.viewmodel.CineFlowViewModel

@Composable
fun ProjectsCloudScreen(
    viewModel: CineFlowViewModel,
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentProject by viewModel.currentProject.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()
    val playheadMs by viewModel.playheadPositionMs.collectAsState()
    val connectedDevices by CloudSyncService.connectedDevices.collectAsState()
    val storageUsedMb by CloudSyncService.cloudStorageUsedMb.collectAsState()

    var showNewProjectDialog by remember { mutableStateOf(false) }
    var newProjectTitle by remember { mutableStateOf("") }

    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteEmail by remember { mutableStateOf("") }

    var commentText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .padding(16.dp)
            .testTag("projects_cloud_screen")
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
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Cloud Hub & Collaboration",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time sync across devices and team review studio",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            // Cloud Storage Usage Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cloud Storage Quota", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            onClick = { viewModel.triggerAutoCloudSync() },
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E2438),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3652))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = if (isSyncing) CinemaPrimaryCyan else CinemaSuccessGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSyncing) "Syncing..." else "Sync Now",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val storageFraction = storageUsedMb.toFloat() / CloudSyncService.cloudStorageTotalMb
                    LinearProgressIndicator(
                        progress = { storageFraction },
                        color = CinemaPrimaryCyan,
                        trackColor = Color(0xFF262C42),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${storageUsedMb / 1000f} GB used / 50.0 GB Pro Tier",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Cloud Project ID: ${currentProject.cloudProjectId}",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Connected Devices
            Text("Synced User Devices", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                connectedDevices.forEach { device ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (device.isCurrentDevice) Color(0xFF1B2238) else Color(0xFF131522)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (device.isCurrentDevice) CinemaPrimaryCyan else Color(0xFF22263D)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Devices, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(device.type, color = CinemaPrimaryCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(device.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(device.lastSync, color = Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Real-Time Collaborators Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Real-Time Collaborators", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Surface(
                    onClick = { showInviteDialog = true },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF281C3D),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CinemaSecondaryPurple)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = CinemaSecondaryPurple, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Team", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentProject.collaborators.forEach { collaborator ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar with online status dot
                            Box {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(collaborator.colorHex),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            collaborator.avatarInitial,
                                            color = Color(0xFF090A0F),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(if (collaborator.isOnline) CinemaSuccessGreen else Color(0xFF64748B))
                                        .border(1.5.dp, Color(0xFF131522), CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(collaborator.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        collaborator.role,
                                        color = Color(collaborator.colorHex),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .background(Color(0xFF1E2235), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    if (collaborator.isOnline) "Scrubbing at ${formatTimecode(collaborator.activeTimecodeMs, 24)}" else "Offline",
                                    color = if (collaborator.isOnline) CinemaSuccessGreen else Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Revision Notes & Timecode Comments
            Text("Timeline Comments & Director Notes", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Add Comment Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text("Add note at ${formatTimecode(playheadMs, 24)}...", color = Color(0xFF64748B), fontSize = 12.sp)
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaPrimaryCyan,
                        unfocusedBorderColor = Color(0xFF2C324B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFCBD5E1),
                        cursorColor = CinemaPrimaryCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            viewModel.addComment(commentText)
                            commentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CinemaPrimaryCyan)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF090A0F), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comments List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentProject.comments.forEach { comment ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.seekTo(comment.timecodeMs)
                                onNavigateToStudio()
                            }
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(comment.authorName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formatTimecode(comment.timecodeMs, 24),
                                        color = CinemaPrimaryCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier
                                            .background(Color(0xFF003840), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(comment.timestamp, color = Color(0xFF64748B), fontSize = 9.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(comment.message, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Projects Library Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Studio Projects (${projects.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Surface(
                    onClick = { showNewProjectDialog = true },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF003840),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CinemaPrimaryCyan)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Project", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                projects.forEach { proj ->
                    val isCurrent = proj.id == currentProject.id
                    Card(
                        onClick = {
                            viewModel.selectProject(proj)
                            onNavigateToStudio()
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) Color(0xFF1B2238) else Color(0xFF131522)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isCurrent) 1.5.dp else 1.dp,
                            if (isCurrent) CinemaPrimaryCyan else Color(0xFF22263D)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(proj.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "${proj.resolution.label} • ${proj.aspectRatio.label} • ${proj.cloudSyncTimestamp}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                            if (isCurrent) {
                                Text(
                                    "ACTIVE",
                                    color = CinemaPrimaryCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier
                                        .background(Color(0xFF003840), RoundedCornerShape(3.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text("Create New Project", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newProjectTitle,
                    onValueChange = { newProjectTitle = it },
                    placeholder = { Text("e.g. Blade Runner 2099", color = Color(0xFF64748B)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaPrimaryCyan,
                        unfocusedBorderColor = Color(0xFF2D334D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFCBD5E1)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = if (newProjectTitle.isNotBlank()) newProjectTitle else "Untitled Studio Project"
                        viewModel.createNewProject(title)
                        showNewProjectDialog = false
                        newProjectTitle = ""
                        onNavigateToStudio()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimaryCyan)
                ) {
                    Text("Create", color = Color(0xFF090A0F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131522)
        )
    }

    // Invite Collaborator Dialog
    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Add Collaborator", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter email of filmmaker or editor to grant real-time timeline access:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        placeholder = { Text("director@production.com", color = Color(0xFF64748B)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CinemaSecondaryPurple,
                            unfocusedBorderColor = Color(0xFF2D334D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInviteDialog = false
                        inviteEmail = ""
                        viewModel.triggerAutoCloudSync()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CinemaSecondaryPurple)
                ) {
                    Text("Send Invite", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInviteDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131522)
        )
    }
}
