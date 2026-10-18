package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.models.AspectRatioSetting
import com.example.models.CameraMotion
import com.example.models.DEFAULT_STYLE_PRESETS
import com.example.models.StyleCategory
import com.example.models.StylePreset
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.CinemaSecondaryPurple
import com.example.viewmodel.CineFlowViewModel

enum class GenerationMode(val label: String) {
    TEXT_TO_VIDEO("Prompt to Video"),
    PHOTO_TO_VIDEO("Animate Photo (Veo)")
}

@Composable
fun GenerateScreen(
    viewModel: CineFlowViewModel,
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(GenerationMode.TEXT_TO_VIDEO) }

    // Text-to-Video state
    val prompt by viewModel.promptInput.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val selectedCamera by viewModel.selectedCameraMotion.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationStep by viewModel.generationStep.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()

    // Photo-to-Video state
    val selectedPhotoUri by viewModel.selectedPhotoUri.collectAsState()
    val photoPrompt by viewModel.photoAnimationPrompt.collectAsState()
    val photoAspect by viewModel.photoAspectRatio.collectAsState()
    val photoCamera by viewModel.photoCameraMotion.collectAsState()
    val isPhotoAnimating by viewModel.isPhotoAnimating.collectAsState()
    val photoAnimateStep by viewModel.photoAnimateStep.collectAsState()

    var activeCategory by remember { mutableStateOf(StyleCategory.CINEMATIC) }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.selectedPhotoUri.value = uri.toString()
        }
    }

    val promptSuggestions = listOf(
        "Neon rain-slicked cyberpunk alley with flying vehicles",
        "Aerial alpine mountain lake reflection at golden hour",
        "Macro shot of mechanical petals blooming in slow motion",
        "Sci-Fi spacecraft navigating through a glowing asteroid field"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .padding(16.dp)
            .testTag("generate_screen")
    ) {
        item {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF003840),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI Video Generator",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create clips from text prompts or animate photos with Veo",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            // Segmented Mode Switcher: Prompt to Video vs Animate Photo (Veo)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141724))
                    .border(1.dp, Color(0xFF262C42), RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                GenerationMode.values().forEach { mode ->
                    val isSelected = activeMode == mode
                    Surface(
                        onClick = { activeMode = mode },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CinemaPrimaryCyan else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.label,
                                color = if (isSelected) Color(0xFF090A0F) else Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (activeMode == GenerationMode.PHOTO_TO_VIDEO) {
            // ================= PHOTO TO VIDEO MODE (Veo) =================
            item {
                // Photo Upload Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF23283E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedPhotoUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF000000))
                            ) {
                                AsyncImage(
                                    model = selectedPhotoUri,
                                    contentDescription = "Uploaded Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { viewModel.selectedPhotoUri.value = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(30.dp)
                                        .background(Color(0x99000000), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                onClick = {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1F2438)
                            ) {
                                Text("Choose Different Photo", color = CinemaPrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        } else {
                            Surface(
                                onClick = {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF181C2C),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.linearGradient(listOf(CinemaPrimaryCyan, CinemaSecondaryPurple))),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("upload_photo_button")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Upload Photo to Animate", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Supports portraits, concept art, and landscapes", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Powered by Veo (veo-3.1-fast-generate-preview)", color = CinemaAccentMagenta, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Aspect Ratio Selector (Strictly 16:9 or 9:16)
                Text("Output Aspect Ratio (Veo Standard)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val allowedAspects = listOf(
                        AspectRatioSetting.RATIO_16_9 to "16:9 Landscape (Cinematic / YouTube)",
                        AspectRatioSetting.RATIO_9_16 to "9:16 Portrait (TikTok / Reels / Shorts)"
                    )
                    allowedAspects.forEach { (aspect, desc) ->
                        val isSelected = photoAspect == aspect
                        Card(
                            onClick = { viewModel.photoAspectRatio.value = aspect },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E243B) else Color(0xFF131522)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) CinemaPrimaryCyan else Color(0xFF242A40)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(aspect.label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(desc, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motion & Animation Guidance Prompt
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22263D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Motion Guidance Prompt", color = Color(0xFFCBD5E1), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = photoPrompt,
                            onValueChange = { viewModel.photoAnimationPrompt.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("photo_prompt_input"),
                            placeholder = { Text("e.g. Slow atmospheric push-in, hair blowing in the wind, subtle lighting shift...", color = Color(0xFF64748B), fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CinemaPrimaryCyan,
                                unfocusedBorderColor = Color(0xFF2D334D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color(0xFFCBD5E1)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Camera Motion
                Text("Camera Motion", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CameraMotion.values()) { motion ->
                        val isSelected = photoCamera == motion
                        Surface(
                            onClick = { viewModel.photoCameraMotion.value = motion },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF252B44) else Color(0xFF131522),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) CinemaAccentMagenta else Color(0xFF24293D)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(motion.label, color = if (isSelected) Color.White else Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(motion.iconDesc, color = Color(0xFF64748B), fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Animate Button or Progress
                if (isPhotoAnimating) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF171A2B)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaPrimaryCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = CinemaPrimaryCyan, strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(photoAnimateStep, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.animatePhotoToVideo()
                            onNavigateToStudio()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("animate_photo_veo_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimaryCyan)
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFF090A0F), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Animate with Veo (${photoAspect.label})",
                            color = Color(0xFF090A0F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // ================= TEXT TO VIDEO MODE =================
            item {
                // Prompt Input Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22273D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Describe your visual scene",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = prompt,
                            onValueChange = { viewModel.promptInput.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("prompt_input_field"),
                            placeholder = {
                                Text("e.g. Cinematic wide angle of an astronaut exploring crystalline caves on alien world...", color = Color(0xFF64748B), fontSize = 13.sp)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CinemaPrimaryCyan,
                                unfocusedBorderColor = Color(0xFF2D334D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color(0xFFE2E8F0),
                                cursorColor = CinemaPrimaryCyan
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // AI Prompt Enhancer Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${prompt.length} characters",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )

                            Surface(
                                onClick = { viewModel.enhancePromptWithAi() },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2A1C47),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CinemaSecondaryPurple),
                                modifier = Modifier.testTag("enhance_prompt_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CinemaPrimaryCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AI Director Enhance",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Prompt Ideas
                Text("Inspiration prompts:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(promptSuggestions) { suggestion ->
                        Surface(
                            onClick = { viewModel.promptInput.value = suggestion },
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF191C2B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF293047))
                        ) {
                            Text(
                                text = suggestion,
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Style Customization Category Header
                Text(
                    text = "Style Customization",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Style Category Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StyleCategory.values().forEach { cat ->
                        val isSelected = activeCategory == cat
                        Surface(
                            onClick = { activeCategory = cat },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CinemaPrimaryCyan else Color(0xFF141724),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CinemaPrimaryCyan else Color(0xFF272D45)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = cat.displayName,
                                    color = if (isSelected) Color(0xFF090A0F) else Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Style Presets for active category
                val stylesForCategory = DEFAULT_STYLE_PRESETS.filter { it.category == activeCategory }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    stylesForCategory.forEach { style ->
                        val isSelected = selectedStyle.id == style.id
                        Card(
                            onClick = { viewModel.selectedStyle.value = style },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E2338) else Color(0xFF131522)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) CinemaPrimaryCyan else Color(0xFF23273D)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("style_${style.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(style.primaryColor), Color(style.secondaryColor))
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = style.name,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = style.description,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
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

                // Camera Motion Selector
                Text(
                    text = "Camera Movement",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CameraMotion.values()) { motion ->
                        val isSelected = selectedCamera == motion
                        Surface(
                            onClick = { viewModel.selectedCameraMotion.value = motion },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF252B44) else Color(0xFF131522),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) CinemaAccentMagenta else Color(0xFF24293D)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = motion.label,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = motion.iconDesc,
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Generation Progress or Trigger Button
                if (isGenerating) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF171A2B)),
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
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = generationStep,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { generationProgress },
                                color = CinemaPrimaryCyan,
                                trackColor = Color(0xFF282F48),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.generateVideoClip()
                            onNavigateToStudio()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("generate_video_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaPrimaryCyan
                        )
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF090A0F), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generate 4K Clip Sequence",
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
}
