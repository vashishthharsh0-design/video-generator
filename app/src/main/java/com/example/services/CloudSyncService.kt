package com.example.services

import com.example.models.Collaborator
import com.example.models.CollaboratorComment
import com.example.models.VideoProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object CloudSyncService {

    data class CloudDevice(
        val name: String,
        val type: String, // "Tablet", "Workstation", "Mobile"
        val lastSync: String,
        val isCurrentDevice: Boolean
    )

    private val _connectedDevices = MutableStateFlow(
        listOf(
            CloudDevice("Pixel 9 Pro (This Device)", "Mobile", "Active now", true),
            CloudDevice("Mac Studio M3 Max (Studio A)", "Workstation", "Synced 10m ago", false),
            CloudDevice("Pixel Tablet (Director Review)", "Tablet", "Synced 1h ago", false)
        )
    )
    val connectedDevices: StateFlow<List<CloudDevice>> = _connectedDevices.asStateFlow()

    private val _cloudStorageUsedMb = MutableStateFlow(4820) // ~4.8 GB
    val cloudStorageUsedMb: StateFlow<Int> = _cloudStorageUsedMb.asStateFlow()
    val cloudStorageTotalMb = 50000 // 50 GB Free Tier

    fun createDefaultCollaborators(): List<Collaborator> {
        return listOf(
            Collaborator(
                id = "c1",
                name = "Elena Vance",
                email = "elena.vance@cineflow.studio",
                role = "Director",
                avatarInitial = "EV",
                colorHex = 0xFF00E5FF,
                isOnline = true,
                activeTimecodeMs = 4200L
            ),
            Collaborator(
                id = "c2",
                name = "Marcus Chen",
                email = "marcus.chen@soundstage.io",
                role = "Sound & Color",
                avatarInitial = "MC",
                colorHex = 0xFFFF2A85,
                isOnline = true,
                activeTimecodeMs = 8500L
            ),
            Collaborator(
                id = "c3",
                name = "Sarah Miller",
                email = "s.miller@vfxhouse.tv",
                role = "VFX Editor",
                avatarInitial = "SM",
                colorHex = 0xFFFFB300,
                isOnline = false,
                activeTimecodeMs = 0L
            )
        )
    }

    fun createDefaultComments(): List<CollaboratorComment> {
        return listOf(
            CollaboratorComment(
                authorName = "Elena Vance (Director)",
                timecodeMs = 4000L,
                message = "Love the transition into the neon corridor! Let's bump speed to 1.25x.",
                timestamp = "12m ago"
            ),
            CollaboratorComment(
                authorName = "Marcus Chen (Sound)",
                timecodeMs = 8000L,
                message = "AI noise reduction on track 2 reduced ambient fan hum by -18dB cleanly.",
                timestamp = "3m ago"
            )
        )
    }
}
