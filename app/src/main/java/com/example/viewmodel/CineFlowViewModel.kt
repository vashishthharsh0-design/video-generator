package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.AspectRatioSetting
import com.example.models.AudioTrackItem
import com.example.models.CameraMotion
import com.example.models.CollaboratorComment
import com.example.models.DEFAULT_STYLE_PRESETS
import com.example.models.EffectItem
import com.example.models.ExportResolution
import com.example.models.LiveChatMessage
import com.example.models.LutFilter
import com.example.models.LyriaMusicModel
import com.example.models.NoiseReductionConfig
import com.example.models.StylePreset
import com.example.models.SubtitleItem
import com.example.models.TransitionType
import com.example.models.VideoClip
import com.example.models.VideoCodec
import com.example.models.VideoProject
import com.example.services.CloudSyncService
import com.example.services.GeminiVideoService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CineFlowViewModel : ViewModel() {

    // Dark mode state
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Projects list
    private val _projects = MutableStateFlow<List<VideoProject>>(emptyList())
    val projects: StateFlow<List<VideoProject>> = _projects.asStateFlow()

    // Current active project
    private val _currentProject = MutableStateFlow(createInitialProject())
    val currentProject: StateFlow<VideoProject> = _currentProject.asStateFlow()

    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playheadPositionMs = MutableStateFlow(0L)
    val playheadPositionMs: StateFlow<Long> = _playheadPositionMs.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f)
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private var playbackJob: Job? = null

    // Generation tab state
    val promptInput = MutableStateFlow("A cinematic drone sweep over neon cyber towers in rain, reflection in puddles, atmospheric haze")
    val selectedStyle = MutableStateFlow(DEFAULT_STYLE_PRESETS.first { it.id == "cine_noir" })
    val selectedCameraMotion = MutableStateFlow(CameraMotion.DOLLY_IN)
    val selectedDurationSeconds = MutableStateFlow(5)

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStep = MutableStateFlow("")
    val generationStep: StateFlow<String> = _generationStep.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress: StateFlow<Float> = _generationProgress.asStateFlow()

    // Photo to Video (veo-3.1-fast-generate-preview)
    val selectedPhotoUri = MutableStateFlow<String?>(null)
    val photoAnimationPrompt = MutableStateFlow("Cinematic push-in with atmospheric volumetric light, realistic particles, and subtle camera drift")
    val photoAspectRatio = MutableStateFlow(AspectRatioSetting.RATIO_16_9) // 16:9 or 9:16
    val photoCameraMotion = MutableStateFlow(CameraMotion.DOLLY_IN)
    private val _isPhotoAnimating = MutableStateFlow(false)
    val isPhotoAnimating: StateFlow<Boolean> = _isPhotoAnimating.asStateFlow()
    private val _photoAnimateStep = MutableStateFlow("")
    val photoAnimateStep: StateFlow<String> = _photoAnimateStep.asStateFlow()

    // Lyria Music Generation (lyria-3-clip-preview & lyria-3-pro-preview)
    val musicPrompt = MutableStateFlow("Cinematic synthwave soundtrack with driving percussion and soaring neon melody")
    val selectedLyriaModel = MutableStateFlow(LyriaMusicModel.LYRIA_CLIP)
    private val _isGeneratingMusic = MutableStateFlow(false)
    val isGeneratingMusic: StateFlow<Boolean> = _isGeneratingMusic.asStateFlow()

    // Live Voice Conversations (gemini-3.8-live)
    private val _liveMessages = MutableStateFlow(
        listOf(
            LiveChatMessage(
                sender = "AI Director (gemini-3.8-live)",
                text = "Welcome to CineFlow Live Studio! I'm your real-time co-director powered by gemini-3.8-live. Talk with me about shot composition, transitions, sound effects, or lighting setups.",
                isUser = false
            )
        )
    )
    val liveMessages: StateFlow<List<LiveChatMessage>> = _liveMessages.asStateFlow()
    private val _isLiveListening = MutableStateFlow(false)
    val isLiveListening: StateFlow<Boolean> = _isLiveListening.asStateFlow()
    private val _isLiveThinking = MutableStateFlow(false)
    val isLiveThinking: StateFlow<Boolean> = _isLiveThinking.asStateFlow()

    // Audio & Noise Reduction state
    private val _compareOriginalAudio = MutableStateFlow(false)
    val compareOriginalAudio: StateFlow<Boolean> = _compareOriginalAudio.asStateFlow()

    private val _spectralSpectrum = MutableStateFlow(
        listOf(0.85f, 0.72f, 0.90f, 0.65f, 0.40f, 0.88f, 0.95f, 0.50f, 0.35f, 0.20f, 0.15f, 0.10f)
    )
    val spectralSpectrum: StateFlow<List<Float>> = _spectralSpectrum.asStateFlow()

    // Export pipeline state
    val exportResolution = MutableStateFlow(ExportResolution.RES_4K)
    val exportAspectRatio = MutableStateFlow(AspectRatioSetting.RATIO_16_9)
    val exportCodec = MutableStateFlow(VideoCodec.H264)
    val exportFps = MutableStateFlow(24)

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportProgress = MutableStateFlow(0f)
    val exportProgress: StateFlow<Float> = _exportProgress.asStateFlow()

    private val _exportStage = MutableStateFlow("")
    val exportStage: StateFlow<String> = _exportStage.asStateFlow()

    private val _exportedFilePath = MutableStateFlow<String?>(null)
    val exportedFilePath: StateFlow<String?> = _exportedFilePath.asStateFlow()

    // Cloud sync state
    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    init {
        val initial = _currentProject.value
        _projects.value = listOf(
            initial,
            VideoProject(
                title = "Alpine Solitude (4K Documentary)",
                prompt = "Misty glacial mountains at blue hour, pristine mirror lake reflection",
                stylePresetId = "live_hyper",
                aspectRatio = AspectRatioSetting.RATIO_21_9,
                resolution = ExportResolution.RES_4K,
                cloudSyncTimestamp = "Synced 1h ago"
            ),
            VideoProject(
                title = "Chrono Mecha (Anime Action)",
                prompt = "Futuristic android awakening in overgrown ancient ruins, pollen particles",
                stylePresetId = "anim_anime",
                aspectRatio = AspectRatioSetting.RATIO_9_16,
                resolution = ExportResolution.RES_1080P,
                cloudSyncTimestamp = "Synced yesterday"
            )
        )
        if (initial.clips.isNotEmpty()) {
            _selectedClipId.value = initial.clips.first().id
        }
        startSpectralSimulator()
    }

    private fun createInitialProject(): VideoProject {
        val clips = listOf(
            VideoClip(
                title = "Intro: Neon Skyline",
                prompt = "Cyberpunk high-rise towers glowing in rain at dusk, flying speeders",
                durationMs = 4500L,
                startOffsetMs = 0L,
                speedMultiplier = 1.0f,
                transitionIn = TransitionType.NONE,
                stylePresetId = "cine_noir",
                cameraMotion = CameraMotion.DOLLY_IN,
                lutFilter = LutFilter.TEAL_ORANGE,
                primaryColor = 0xFF00E5FF,
                secondaryColor = 0xFF7C4DFF,
                simulatedSceneTag = "Cyberpunk Skyline"
            ),
            VideoClip(
                title = "Scene 2: Alleyway Reflections",
                prompt = "Wet asphalt street reflecting holographic billboards, steam rising",
                durationMs = 5000L,
                startOffsetMs = 4500L,
                speedMultiplier = 1.0f,
                transitionIn = TransitionType.CROSSFADE,
                stylePresetId = "cine_flare",
                cameraMotion = CameraMotion.PAN_RIGHT,
                lutFilter = LutFilter.NEO_NOIR,
                primaryColor = 0xFFFF2A85,
                secondaryColor = 0xFF536DFE,
                simulatedSceneTag = "Neon Alley Reflection"
            ),
            VideoClip(
                title = "Scene 3: Hovercar Accelerating",
                prompt = "Sleek aerodynamic hovercar speeding into glowing sub-level tunnel",
                durationMs = 4000L,
                startOffsetMs = 9500L,
                speedMultiplier = 1.25f,
                transitionIn = TransitionType.GLITCH,
                stylePresetId = "cine_scifi",
                cameraMotion = CameraMotion.DRONE_FPV,
                lutFilter = LutFilter.CYBER_MATRIX,
                primaryColor = 0xFF00E676,
                secondaryColor = 0xFF00E5FF,
                simulatedSceneTag = "Tunnel Acceleration"
            ),
            VideoClip(
                title = "Outro: City Dawn",
                prompt = "Sun rising through purple smog clouds, silhouetted skyscrapers",
                durationMs = 4500L,
                startOffsetMs = 13500L,
                speedMultiplier = 0.8f,
                transitionIn = TransitionType.DISSOLVE,
                stylePresetId = "cine_golden",
                cameraMotion = CameraMotion.CRANE_UP,
                lutFilter = LutFilter.VINTAGE_WARM,
                primaryColor = 0xFFFFAB00,
                secondaryColor = 0xFFFF5252,
                simulatedSceneTag = "Dawn Horizon"
            )
        )

        val totalDur = clips.sumOf { (it.durationMs / it.speedMultiplier).toLong() }

        val audio = listOf(
            AudioTrackItem(
                title = "Synthwave Noir OST",
                type = "Soundtrack",
                durationMs = totalDur,
                volume = 0.8f,
                isNoiseReduced = true
            ),
            AudioTrackItem(
                title = "AI Voiceover: City Dispatch",
                type = "AI Voiceover",
                durationMs = 12000L,
                startOffsetMs = 1000L,
                volume = 0.95f,
                isNoiseReduced = true
            )
        )

        val subs = listOf(
            SubtitleItem(
                text = "In Sector 4, the rain never truly washes away the neon.",
                startMs = 1200L,
                endMs = 5200L
            ),
            SubtitleItem(
                text = "Units responding to energy pulse at Level 9.",
                startMs = 6000L,
                endMs = 10500L
            ),
            SubtitleItem(
                text = "Engaging hyper-drive in three... two...",
                startMs = 11000L,
                endMs = 15000L
            )
        )

        val effects = listOf(
            EffectItem(
                name = "Anamorphic Blue Flare",
                type = "Lens Flare",
                startMs = 0L,
                endMs = 8000L,
                intensity = 0.75f
            ),
            EffectItem(
                name = "35mm Film Grain",
                type = "Film Grain",
                startMs = 0L,
                endMs = totalDur,
                intensity = 0.4f
            )
        )

        return VideoProject(
            title = "Neo Tokyo: Cyber Symphony",
            prompt = "A high-fidelity cinematic video of a neon rain-slicked metropolis with flying vehicles and glowing steam",
            stylePresetId = "cine_noir",
            resolution = ExportResolution.RES_4K,
            aspectRatio = AspectRatioSetting.RATIO_16_9,
            clips = clips,
            audioTracks = audio,
            subtitleTracks = subs,
            effectTracks = effects,
            collaborators = CloudSyncService.createDefaultCollaborators(),
            comments = CloudSyncService.createDefaultComments()
        )
    }

    private fun startSpectralSimulator() {
        viewModelScope.launch {
            while (true) {
                delay(120)
                if (_isPlaying.value) {
                    val isClean = !_compareOriginalAudio.value && _currentProject.value.noiseReduction.isEnabled
                    _spectralSpectrum.value = List(12) { index ->
                        val base = if (isClean) {
                            // Suppressed noise floor, clean speech peak in middle bands
                            if (index in 3..7) (0.6f..0.95f) else (0.05f..0.25f)
                        } else {
                            // Noisy raw spectrum with high rumble at lows and hiss at highs
                            (0.45f..0.98f)
                        }
                        (base.start + Math.random().toFloat() * (base.endInclusive - base.start)).coerceIn(0.05f, 1.0f)
                    }
                }
            }
        }
    }

    // Playback control
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val totalDuration = getTotalDurationMs()
            while (_isPlaying.value) {
                delay(50)
                val nextPos = _playheadPositionMs.value + 50
                if (nextPos >= totalDuration) {
                    _playheadPositionMs.value = 0L // loop
                } else {
                    _playheadPositionMs.value = nextPos
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(positionMs: Long) {
        val total = getTotalDurationMs()
        _playheadPositionMs.value = positionMs.coerceIn(0L, total.coerceAtLeast(1000L))
    }

    fun stepFrame(forward: Boolean) {
        val stepMs = 1000L / _currentProject.value.framerate
        val newPos = if (forward) _playheadPositionMs.value + stepMs else _playheadPositionMs.value - stepMs
        seekTo(newPos)
    }

    fun getTotalDurationMs(): Long {
        val project = _currentProject.value
        if (project.clips.isEmpty()) return 5000L
        return project.clips.sumOf { (it.durationMs / it.speedMultiplier).toLong() }
    }

    fun selectClip(clipId: String) {
        _selectedClipId.value = clipId
    }

    fun setZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 3.0f)
    }

    fun setProjectAspectRatio(aspect: AspectRatioSetting) {
        _currentProject.update { it.copy(aspectRatio = aspect, lastModified = System.currentTimeMillis()) }
        exportAspectRatio.value = aspect
    }

    // Timeline editing actions
    fun splitClipAtPlayhead() {
        val playhead = _playheadPositionMs.value
        val project = _currentProject.value
        var accumulated = 0L

        val updatedClips = mutableListOf<VideoClip>()
        var didSplit = false

        for (clip in project.clips) {
            val clipDurationOnTimeline = (clip.durationMs / clip.speedMultiplier).toLong()
            val clipEnd = accumulated + clipDurationOnTimeline

            if (!didSplit && playhead > accumulated && playhead < clipEnd) {
                // Split this clip
                val offsetInClipMs = ((playhead - accumulated) * clip.speedMultiplier).toLong()
                val remainingMs = clip.durationMs - offsetInClipMs

                val firstHalf = clip.copy(
                    id = UUID.randomUUID().toString(),
                    title = "${clip.title} (Part 1)",
                    durationMs = offsetInClipMs
                )
                val secondHalf = clip.copy(
                    id = UUID.randomUUID().toString(),
                    title = "${clip.title} (Part 2)",
                    durationMs = remainingMs,
                    transitionIn = TransitionType.CROSSFADE
                )

                updatedClips.add(firstHalf)
                updatedClips.add(secondHalf)
                _selectedClipId.value = secondHalf.id
                didSplit = true
            } else {
                updatedClips.add(clip)
            }
            accumulated += clipDurationOnTimeline
        }

        if (didSplit) {
            _currentProject.update { it.copy(clips = updatedClips, lastModified = System.currentTimeMillis()) }
            triggerAutoCloudSync()
        }
    }

    fun deleteClip(clipId: String) {
        val project = _currentProject.value
        val newClips = project.clips.filter { it.id != clipId }
        _currentProject.update { it.copy(clips = newClips, lastModified = System.currentTimeMillis()) }
        if (_selectedClipId.value == clipId) {
            _selectedClipId.value = newClips.firstOrNull()?.id
        }
        triggerAutoCloudSync()
    }

    fun duplicateClip(clipId: String) {
        val project = _currentProject.value
        val index = project.clips.indexOfFirst { it.id == clipId }
        if (index != -1) {
            val clip = project.clips[index]
            val duplicate = clip.copy(
                id = UUID.randomUUID().toString(),
                title = "${clip.title} (Copy)"
            )
            val mutable = project.clips.toMutableList()
            mutable.add(index + 1, duplicate)
            _currentProject.update { it.copy(clips = mutable, lastModified = System.currentTimeMillis()) }
            _selectedClipId.value = duplicate.id
            triggerAutoCloudSync()
        }
    }

    fun updateClipSpeed(clipId: String, speed: Float) {
        _currentProject.update { project ->
            project.copy(
                clips = project.clips.map { if (it.id == clipId) it.copy(speedMultiplier = speed) else it },
                lastModified = System.currentTimeMillis()
            )
        }
    }

    fun updateClipTransition(clipId: String, transition: TransitionType) {
        _currentProject.update { project ->
            project.copy(
                clips = project.clips.map { if (it.id == clipId) it.copy(transitionIn = transition) else it },
                lastModified = System.currentTimeMillis()
            )
        }
    }

    fun updateClipLut(clipId: String, lut: LutFilter) {
        _currentProject.update { project ->
            project.copy(
                clips = project.clips.map { if (it.id == clipId) it.copy(lutFilter = lut) else it },
                lastModified = System.currentTimeMillis()
            )
        }
    }

    fun updateClipDuration(clipId: String, durationMs: Long) {
        _currentProject.update { project ->
            project.copy(
                clips = project.clips.map { if (it.id == clipId) it.copy(durationMs = durationMs) else it },
                lastModified = System.currentTimeMillis()
            )
        }
    }

    // AI Generation workflow
    fun enhancePromptWithAi() {
        viewModelScope.launch {
            _isGenerating.value = true
            _generationStep.value = "AI Director: Enhancing cinematic prompt..."
            val raw = promptInput.value
            val enhanced = GeminiVideoService.enhancePrompt(raw, selectedStyle.value, selectedCameraMotion.value)
            promptInput.value = enhanced
            _isGenerating.value = false
            _generationStep.value = ""
        }
    }

    fun generateVideoClip() {
        viewModelScope.launch {
            _isGenerating.value = true
            _generationProgress.value = 0.05f
            _generationStep.value = "1/5: Analyzing cinematic prompt & visual style..."

            delay(600)
            _generationProgress.value = 0.25f
            _generationStep.value = "2/5: Synthesizing ${selectedStyle.value.name} diffusion latents..."

            delay(800)
            _generationProgress.value = 0.55f
            _generationStep.value = "3/5: Rendering 4K frames with ${selectedCameraMotion.value.label} motion..."

            delay(900)
            _generationProgress.value = 0.80f
            _generationStep.value = "4/5: Applying neural background noise reduction & audio sync..."

            delay(600)
            _generationProgress.value = 0.95f
            _generationStep.value = "5/5: Adding generated sequence to studio timeline..."

            val newClips = GeminiVideoService.generateClipSequence(promptInput.value, selectedStyle.value)
            val currentClips = _currentProject.value.clips.toMutableList()
            currentClips.addAll(newClips)

            _currentProject.update {
                it.copy(
                    clips = currentClips,
                    lastModified = System.currentTimeMillis()
                )
            }

            if (newClips.isNotEmpty()) {
                _selectedClipId.value = newClips.first().id
            }

            delay(300)
            _generationProgress.value = 1.0f
            _generationStep.value = "Generated 4 clips successfully!"
            delay(500)
            _isGenerating.value = false
            _generationProgress.value = 0f
            _generationStep.value = ""
            triggerAutoCloudSync()
        }
    }

    // Noise reduction controls
    fun toggleNoiseReduction(enabled: Boolean) {
        _currentProject.update {
            it.copy(
                noiseReduction = it.noiseReduction.copy(isEnabled = enabled),
                lastModified = System.currentTimeMillis()
            )
        }
    }

    fun setNoiseReductionDb(db: Int) {
        _currentProject.update {
            it.copy(
                noiseReduction = it.noiseReduction.copy(reductionLevelDb = db),
                lastModified = System.currentTimeMillis()
            )
        }
    }

    fun toggleAudioComparison(compareOriginal: Boolean) {
        _compareOriginalAudio.value = compareOriginal
    }

    fun updateNoiseReductionPreset(presetName: String, db: Int, hum: Boolean, wind: Boolean, dereverb: Boolean) {
        _currentProject.update {
            it.copy(
                noiseReduction = it.noiseReduction.copy(
                    algorithmName = presetName,
                    reductionLevelDb = db,
                    removeRoomHum = hum,
                    removeWindNoise = wind,
                    deReverbEnabled = dereverb
                ),
                lastModified = System.currentTimeMillis()
            )
        }
    }

    // Collaboration & Cloud
    fun addComment(message: String) {
        if (message.isBlank()) return
        val newComment = CollaboratorComment(
            authorName = "You (Editor)",
            timecodeMs = _playheadPositionMs.value,
            message = message.trim(),
            timestamp = "Just now"
        )
        val updatedComments = listOf(newComment) + _currentProject.value.comments
        _currentProject.update { it.copy(comments = updatedComments) }
        triggerAutoCloudSync()
    }

    fun triggerAutoCloudSync() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            delay(600)
            _currentProject.update {
                it.copy(
                    isCloudSynced = true,
                    cloudSyncTimestamp = "Synced just now"
                )
            }
            _isCloudSyncing.value = false
        }
    }

    // Export pipeline
    fun startExport() {
        viewModelScope.launch {
            _isExporting.value = true
            _exportProgress.value = 0.05f
            _exportedFilePath.value = null

            _exportStage.value = "Initializing 4K render pipeline..."
            delay(500)

            _exportProgress.value = 0.20f
            _exportStage.value = "Analyzing multi-track frames & color LUT..."
            delay(700)

            _exportProgress.value = 0.45f
            _exportStage.value = "Encoding ${exportResolution.value.label} (${exportAspectRatio.value.label}) at ${exportFps.value}fps..."
            delay(900)

            _exportProgress.value = 0.70f
            _exportStage.value = "Applying AI background noise reduction & audio master..."
            delay(800)

            _exportProgress.value = 0.90f
            _exportStage.value = "Muxing ${exportCodec.value.container} video stream & container..."
            delay(600)

            _exportProgress.value = 1.0f
            _exportStage.value = "Render Complete!"
            _exportedFilePath.value = "/storage/emulated/0/Movies/CineFlow_${System.currentTimeMillis()}_${exportResolution.value.name}.${exportCodec.value.container.lowercase()}"
            delay(400)
            _isExporting.value = false
        }
    }

    fun dismissExport() {
        _isExporting.value = false
        _exportProgress.value = 0f
        _exportStage.value = ""
    }

    fun createNewProject(title: String = "Untitled Studio Project") {
        val newProj = VideoProject(
            title = title,
            prompt = "A high-concept cinematic scene with atmospheric depth",
            stylePresetId = "cine_flare",
            resolution = ExportResolution.RES_4K,
            aspectRatio = AspectRatioSetting.RATIO_16_9,
            clips = listOf(
                VideoClip(
                    title = "Scene 01: Hero Intro",
                    prompt = "Hero opening shot with dramatic cinematic lighting",
                    durationMs = 4000L,
                    stylePresetId = "cine_flare",
                    primaryColor = 0xFF00E5FF,
                    secondaryColor = 0xFF7C4DFF,
                    simulatedSceneTag = "Hero Shot"
                )
            ),
            collaborators = CloudSyncService.createDefaultCollaborators()
        )
        _projects.value = listOf(newProj) + _projects.value
        _currentProject.value = newProj
        _selectedClipId.value = newProj.clips.first().id
        _playheadPositionMs.value = 0L
    }

    fun selectProject(project: VideoProject) {
        _currentProject.value = project
        _selectedClipId.value = project.clips.firstOrNull()?.id
        _playheadPositionMs.value = 0L
    }

    // Animate uploaded photo using Veo (veo-3.1-fast-generate-preview)
    fun animatePhotoToVideo() {
        viewModelScope.launch {
            _isPhotoAnimating.value = true
            _photoAnimateStep.value = "1/4: Analyzing photo & calculating motion vectors..."
            delay(600)
            _photoAnimateStep.value = "2/4: Initializing Veo (veo-3.1-fast-generate-preview)..."
            delay(800)
            _photoAnimateStep.value = "3/4: Rendering ${photoAspectRatio.value.label} video frames with ${photoCameraMotion.value.label}..."
            delay(900)
            _photoAnimateStep.value = "4/4: Placing animated clip on studio timeline..."

            val newClip = GeminiVideoService.animatePhotoWithVeo(
                imageUri = selectedPhotoUri.value,
                prompt = photoAnimationPrompt.value,
                aspectRatio = photoAspectRatio.value,
                cameraMotion = photoCameraMotion.value
            )

            val currentClips = _currentProject.value.clips.toMutableList()
            currentClips.add(newClip)
            _currentProject.update {
                it.copy(
                    clips = currentClips,
                    lastModified = System.currentTimeMillis()
                )
            }
            _selectedClipId.value = newClip.id

            delay(300)
            _isPhotoAnimating.value = false
            _photoAnimateStep.value = ""
            triggerAutoCloudSync()
        }
    }

    // Generate Music with Lyria (lyria-3-clip-preview & lyria-3-pro-preview)
    fun generateMusicWithLyria() {
        viewModelScope.launch {
            _isGeneratingMusic.value = true
            val track = GeminiVideoService.generateMusicWithLyria(
                prompt = musicPrompt.value,
                model = selectedLyriaModel.value
            )
            val currentAudio = _currentProject.value.audioTracks.toMutableList()
            currentAudio.add(track)
            _currentProject.update {
                it.copy(
                    audioTracks = currentAudio,
                    lastModified = System.currentTimeMillis()
                )
            }
            _isGeneratingMusic.value = false
            triggerAutoCloudSync()
        }
    }

    // Live Voice Conversations (gemini-3.8-live)
    fun sendLiveMessage(message: String) {
        if (message.isBlank()) return
        val userMsg = LiveChatMessage(
            sender = "You",
            text = message.trim(),
            isUser = true
        )
        _liveMessages.update { it + userMsg }

        viewModelScope.launch {
            _isLiveThinking.value = true
            val aiResponse = GeminiVideoService.converseWithLiveApi(userMsg.text, _liveMessages.value)
            _liveMessages.update { it + aiResponse }
            _isLiveThinking.value = false
        }
    }

    fun toggleLiveListening() {
        val willListen = !_isLiveListening.value
        _isLiveListening.value = willListen
        if (willListen) {
            viewModelScope.launch {
                delay(2400) // Simulates voice capture interval
                if (_isLiveListening.value) {
                    _isLiveListening.value = false
                    val voicePrompts = listOf(
                        "Can you suggest a dramatic low-angle camera motion for our second scene?",
                        "What transition works best going from the neon alleyway to the skyline?",
                        "How should we balance the dialog track against Lyria's synthwave soundtrack?"
                    )
                    val randomVoice = voicePrompts[(Math.random() * voicePrompts.size).toInt()]
                    sendLiveMessage(randomVoice)
                }
            }
        }
    }
}
