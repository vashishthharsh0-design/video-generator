package com.example.models

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class StyleCategory(val displayName: String) {
    ANIMATION("Animation"),
    LIVE_ACTION("Live-Action"),
    CINEMATIC("Cinematic Effects")
}

data class StylePreset(
    val id: String,
    val name: String,
    val category: StyleCategory,
    val description: String,
    val primaryColor: Long,
    val secondaryColor: Long,
    val promptModifier: String
)

val DEFAULT_STYLE_PRESETS = listOf(
    // Animation
    StylePreset("anim_anime", "Neo Anime", StyleCategory.ANIMATION, "Makoto Shinkai style, vibrant skies, hand-drawn anime aesthetic", 0xFFFF5376, 0xFF7C4DFF, "masterpiece anime aesthetic, makoto shinkai lighting, vibrant color palette, ultra detailed 4k"),
    StylePreset("anim_3d", "3D Animation", StyleCategory.ANIMATION, "Pixar/Dreamworks high-fidelity stylized 3D character & scene", 0xFFFF9100, 0xFFFF5252, "pixar style 3d animation, octane render, soft subsurface scattering, whimsical volumetric light"),
    StylePreset("anim_clay", "Claymation", StyleCategory.ANIMATION, "Stop-motion tactile clay texture, handcrafted aesthetic", 0xFFE040FB, 0xFF536DFE, "tactile stop-motion claymation, handcrafted clay fingerprints, studio miniature lighting"),
    StylePreset("anim_cyber", "Cyberpunk Cel", StyleCategory.ANIMATION, "Hard-edged cel-shaded neon futurism", 0xFF00E5FF, 0xFFFF007A, "cyberpunk cel shaded, dark outline, neon glow, dystopian futuristic anime"),

    // Live-Action
    StylePreset("live_hyper", "Hyper-Realistic", StyleCategory.LIVE_ACTION, "ARRI Alexa 65 sensor, photorealistic 8K fidelity", 0xFF00E676, 0xFF00B0FF, "shot on ARRI Alexa 65, 8k resolution, extreme photorealism, natural skin tones, depth of field"),
    StylePreset("live_35mm", "35mm Vintage", StyleCategory.LIVE_ACTION, "Kodak Vision3 500T film grain, warm nostalgic tones", 0xFFFFAB00, 0xFF8D6E63, "shot on Kodak Vision3 35mm film, authentic analog film grain, halation, warm tungsten tone"),
    StylePreset("live_doc", "Documentary", StyleCategory.LIVE_ACTION, "Handheld cinema verité, naturalistic ambient light", 0xFF78909C, 0xFF37474F, "documentary style, raw cinema verite, natural authentic lighting, Sony FX9 cinema camera"),
    StylePreset("live_indie", "Indie Film", StyleCategory.LIVE_ACTION, "Sundance aesthetic, soft pastel grades, atmospheric", 0xFF80CBC4, 0xFF26A69A, "sundance indie film aesthetic, soft vintage color grade, atmospheric mood, prime 50mm f1.4"),

    // Cinematic Effects
    StylePreset("cine_flare", "Anamorphic Flares", StyleCategory.CINEMATIC, "Panavision blue horizontal streaks, cinematic 2.39:1 look", 0xFF00E5FF, 0xFF18FFFF, "panavision anamorphic lens flare, horizontal streak bokeh, high cinematic drama"),
    StylePreset("cine_noir", "Neo-Noir Moody", StyleCategory.CINEMATIC, "Blade Runner rain reflections, chiaroscuro contrast", 0xFF7C4DFF, 0xFF00E5FF, "neo-noir aesthetic, chiaroscuro high contrast, rain-slicked wet pavement, cyan and amber neon"),
    StylePreset("cine_golden", "Golden Hour", StyleCategory.CINEMATIC, "Magic hour backlit glow, warm sunset radiance", 0xFFFFD700, 0xFFFF6D00, "magic hour sunset, dramatic backlighting, golden warm rim light, lens dust particles"),
    StylePreset("cine_scifi", "Sci-Fi Void", StyleCategory.CINEMATIC, "Cosmic deep space bioluminescent volumetric lighting", 0xFFE040FB, 0xFF00E5FF, "sci-fi cinematic, bioluminescent glow, deep space void, intergalactic atmosphere")
)

enum class CameraMotion(val label: String, val iconDesc: String) {
    STATIC("Static Cam", "Fixed tripod position"),
    PAN_RIGHT("Pan Right", "Smooth horizontal tracking"),
    ORBIT_360("360° Orbit", "Circular subject rotation"),
    DOLLY_IN("Dolly In", "Slow cinematic push forward"),
    DRONE_FPV("FPV Drone", "Dynamic high-altitude sweep"),
    CRANE_UP("Crane Up", "Vertical rising perspective")
}

enum class TransitionType(val displayName: String) {
    NONE("Cut"),
    CROSSFADE("Crossfade"),
    DISSOLVE("Dissolve"),
    WIPE("Wipe Left"),
    GLITCH("Glitch Shift"),
    ZOOM("Zoom Impact")
}

enum class LutFilter(val displayName: String, val tintColor: Long) {
    NATURAL("Original", 0x00000000),
    TEAL_ORANGE("Teal & Orange", 0x4000B4D8),
    NEO_NOIR("Neo Noir", 0x507C4DFF),
    VINTAGE_WARM("Vintage 70s", 0x45FFA000),
    CYBER_MATRIX("Cyber Cyan", 0x4500E5FF),
    BLEACH_BYPASS("Bleach Bypass", 0x30B0BEC5)
}

enum class AspectRatioSetting(val label: String, val ratioWidth: Float, val ratioHeight: Float, val description: String) {
    RATIO_16_9("16:9", 16f, 9f, "YouTube & TV Standard"),
    RATIO_9_16("9:16", 9f, 16f, "TikTok, Reels & Shorts"),
    RATIO_1_1("1:1", 1f, 1f, "Instagram Square"),
    RATIO_21_9("21:9", 21f, 9f, "Cinemascope Anamorphic"),
    RATIO_4_5("4:5", 4f, 5f, "Social Portrait")
}

enum class ExportResolution(val label: String, val width: Int, val height: Int, val badge: String) {
    RES_720P("720p HD", 1280, 720, "Fast Draft"),
    RES_1080P("1080p FHD", 1920, 1080, "Pro Standard"),
    RES_4K("4K UHD", 3840, 2160, "Ultra Fidelity")
}

enum class VideoCodec(val label: String, val container: String) {
    H264("H.264 (AVC)", "MP4"),
    HEVC("H.265 (HEVC)", "MP4"),
    PRORES("Apple ProRes 422", "MOV")
}

data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val durationMs: Long = 4000L,
    val startOffsetMs: Long = 0L,
    val speedMultiplier: Float = 1.0f,
    val transitionIn: TransitionType = TransitionType.NONE,
    val stylePresetId: String = "cine_flare",
    val cameraMotion: CameraMotion = CameraMotion.DOLLY_IN,
    val lutFilter: LutFilter = LutFilter.NATURAL,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val primaryColor: Long = 0xFF00E5FF,
    val secondaryColor: Long = 0xFF7C4DFF,
    val simulatedSceneTag: String = "Cyber Cityscape",
    val sourceImageUri: String? = null,
    val modelUsed: String = "veo-3.1-fast-generate-preview"
)

enum class LyriaMusicModel(
    val modelId: String,
    val label: String,
    val maxDurationSeconds: Int,
    val description: String
) {
    LYRIA_CLIP("lyria-3-clip-preview", "Lyria 3 Clip", 30, "Short clips up to 30s for stingers & B-roll"),
    LYRIA_PRO("lyria-3-pro-preview", "Lyria 3 Pro", 120, "Full-length cinematic tracks & orchestral scores")
}

data class LiveChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Just now",
    val suggestedAction: String? = null
)

data class AudioTrackItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: String = "Soundtrack", // "Soundtrack", "AI Voiceover", "SFX", "Lyria AI Music"
    val durationMs: Long = 12000L,
    val startOffsetMs: Long = 0L,
    val volume: Float = 0.85f,
    val isMuted: Boolean = false,
    val isNoiseReduced: Boolean = true,
    val waveformSamples: List<Float> = listOf(0.3f, 0.6f, 0.8f, 0.4f, 0.9f, 0.7f, 0.5f, 0.2f, 0.8f, 0.6f, 0.4f, 0.7f),
    val lyriaModel: String? = null
)

data class SubtitleItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val isAiGenerated: Boolean = true
)

data class EffectItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String, // "Lens Flare", "Film Grain", "Letterbox", "Glow"
    val startMs: Long,
    val endMs: Long,
    val intensity: Float = 0.7f
)

data class Collaborator(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val avatarInitial: String,
    val colorHex: Long,
    val isOnline: Boolean,
    val activeTimecodeMs: Long = 0L
)

data class CollaboratorComment(
    val id: String = UUID.randomUUID().toString(),
    val authorName: String,
    val timecodeMs: Long,
    val message: String,
    val timestamp: String = "Just now"
)

data class NoiseReductionConfig(
    val isEnabled: Boolean = true,
    val reductionLevelDb: Int = -18, // -6 to -36 dB
    val algorithmName: String = "Neural Speech & Ambient Isolation",
    val removeRoomHum: Boolean = true,
    val removeWindNoise: Boolean = true,
    val deReverbEnabled: Boolean = true,
    val vocalClarityBoost: Boolean = true,
    val attenuationMeterLevel: Float = 0.75f
)

data class VideoProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Neo Tokyo Odyssey",
    val prompt: String = "A cinematic cyberpunk flying car soaring between rain-slicked neon skyscrapers at night",
    val stylePresetId: String = "cine_noir",
    val resolution: ExportResolution = ExportResolution.RES_4K,
    val aspectRatio: AspectRatioSetting = AspectRatioSetting.RATIO_16_9,
    val framerate: Int = 24, // 24fps cinematic film standard
    val codec: VideoCodec = VideoCodec.H264,
    val clips: List<VideoClip> = emptyList(),
    val audioTracks: List<AudioTrackItem> = emptyList(),
    val subtitleTracks: List<SubtitleItem> = emptyList(),
    val effectTracks: List<EffectItem> = emptyList(),
    val noiseReduction: NoiseReductionConfig = NoiseReductionConfig(),
    val collaborators: List<Collaborator> = emptyList(),
    val comments: List<CollaboratorComment> = emptyList(),
    val isCloudSynced: Boolean = true,
    val cloudSyncTimestamp: String = "Synced 2m ago",
    val cloudProjectId: String = "CF-9482-PRJ",
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)
