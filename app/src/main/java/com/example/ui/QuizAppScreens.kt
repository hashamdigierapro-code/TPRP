package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import com.example.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.HistoryLog
import com.example.data.LeaderboardPlayer
import com.example.data.QuizQuestion
import com.example.viewmodel.QuizUiState
import com.example.viewmodel.QuizViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerBasedShape

// --- Custom Modern App Canvas Icons ---
@Composable
fun MuslimMoonStarIcon(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // 4-pointed sparkle star
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            quadraticTo(w * 0.5f, h * 0.5f, w * 0.85f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.5f, w * 0.5f, h * 0.85f)
            quadraticTo(w * 0.5f, h * 0.5f, w * 0.15f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.5f, w * 0.5f, h * 0.15f)
            close()
        }
        drawPath(path, color = tint)
        
        // Side glowing level ring
        drawCircle(
            color = tint.copy(alpha = 0.3f),
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

@Composable
fun IslamicLanternIcon(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        val p = Path().apply {
            // Trophy Cup main bowl
            moveTo(w * 0.25f, h * 0.2f)
            lineTo(w * 0.75f, h * 0.2f)
            quadraticTo(w * 0.75f, h * 0.62f, w * 0.5f, h * 0.72f)
            quadraticTo(w * 0.25f, h * 0.62f, w * 0.25f, h * 0.2f)
            close()
            
            // Stem & Base
            moveTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.58f, h * 0.72f)
            lineTo(w * 0.58f, h * 0.85f)
            lineTo(w * 0.68f, h * 0.85f)
            lineTo(w * 0.68f, h * 0.9f)
            lineTo(w * 0.32f, h * 0.9f)
            lineTo(w * 0.32f, h * 0.85f)
            lineTo(w * 0.42f, h * 0.85f)
            close()
        }
        drawPath(p, color = tint)
        
        // Trophy Handles
        val leftHandle = Path().apply {
            moveTo(w * 0.25f, h * 0.28f)
            quadraticTo(w * 0.12f, h * 0.35f, w * 0.25f, h * 0.52f)
        }
        val rightHandle = Path().apply {
            moveTo(w * 0.75f, h * 0.28f)
            quadraticTo(w * 0.88f, h * 0.35f, w * 0.75f, h * 0.52f)
        }
        drawPath(leftHandle, color = tint, style = Stroke(width = 2.dp.toPx()))
        drawPath(rightHandle, color = tint, style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
fun KaabaIcon(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // Left Column (2nd Place)
        drawRect(
            color = tint.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.12f, h * 0.45f),
            size = Size(w * 0.22f, h * 0.45f)
        )
        // Center Column (1st Place)
        drawRect(
            color = tint,
            topLeft = Offset(w * 0.39f, h * 0.25f),
            size = Size(w * 0.22f, h * 0.65f)
        )
        // Right Column (3rd Place)
        drawRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.66f, h * 0.55f),
            size = Size(w * 0.22f, h * 0.35f)
        )
        
        // 1st Star on Center Podium top
        drawCircle(
            color = Color(0xFFFFC107),
            radius = w * 0.05f,
            center = Offset(w * 0.5f, h * 0.15f)
        )
    }
}

@Composable
fun MinaretCompassIcon(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // Circular profile head
        drawCircle(
            color = tint,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.33f)
        )
        // Profile shoulders
        val shoulderPath = Path().apply {
            moveTo(w * 0.18f, h * 0.8f)
            quadraticTo(w * 0.18f, h * 0.55f, w * 0.5f, h * 0.55f)
            quadraticTo(w * 0.82f, h * 0.55f, w * 0.82f, h * 0.8f)
            close()
        }
        drawPath(shoulderPath, color = tint)
        
        // Outer decorative circle representing dynamic analytics/tracking gauge
        drawArc(
            color = tint.copy(alpha = 0.4f),
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(w * 0.05f, h * 0.05f),
            size = Size(w * 0.9f, h * 0.9f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

// --- Country Flag Helper ---
val countryFlags = mapOf(
    "Malaysia" to "🇲🇾",
    "India" to "🇮🇳",
    "Pakistan" to "🇵🇰",
    "Saudi Arabia" to "🇸🇦",
    "Saudi" to "🇸🇦",
    "UAE" to "🇦🇪",
    "Egypt" to "🇪🇬"
)

fun getCountryWithFlag(countryName: String): String {
    val flag = countryFlags[countryName] ?: ""
    return if (flag.isNotEmpty()) "$countryName $flag" else countryName
}

// --- Beautiful Attention-Grabbing Crescent Moon Icon ---
@Composable
fun AnimatedAttentionMoon(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "attention_moon")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2.2f

        // Pulsing background glow for grabbing attention
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = 0.15f * glow),
            radius = radius * 1.5f,
            center = center
        )

        val baseMoonColor = Color(0xFFFFF9E6)
        val shadowColor = Color(0xFF0F1221)

        // Draw shadow backing
        drawCircle(
            color = shadowColor,
            radius = radius,
            center = center
        )

        // Draw phase shifts
        if (phase < 0.5f) {
            val subPhase = phase * 2f
            drawCircle(
                color = baseMoonColor,
                radius = radius,
                center = center
            )
            val shadowOffset = radius * (2f * (1f - subPhase))
            drawCircle(
                color = shadowColor,
                radius = radius * 1.05f,
                center = center.copy(x = center.x - shadowOffset)
            )
        } else {
            val subPhase = (phase - 0.5f) * 2f
            drawCircle(
                color = baseMoonColor,
                radius = radius,
                center = center
            )
            val shadowOffset = radius * (2f * subPhase)
            drawCircle(
                color = shadowColor,
                radius = radius * 1.05f,
                center = center.copy(x = center.x + shadowOffset)
            )
        }

        // Beautiful sharp radiant crescent stroke on top
        drawCircle(
            color = Color(0xFFFFC107),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
    }
}

// --- Streak Moon Animation ---

@Composable
fun StreakMoonAnimation(streak: Int, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val orbitRadius = size.width / 2.5f
        
        // Draw Earth
        drawCircle(
            color = Color(0xFF1E88E5), // Earth blue
            radius = size.width / 4f,
            center = center
        )
        // Draw Continents loosely
        drawCircle(
            color = Color(0xFF43A047), // Earth green
            radius = size.width / 6f,
            center = center.copy(x = center.x - 4f, y = center.y + 4f)
        )

        val radian = kotlin.math.PI * orbitAngle / 180.0
        val z = kotlin.math.sin(radian)
        val x = kotlin.math.cos(radian) * orbitRadius
        val y = kotlin.math.sin(radian) * (orbitRadius * 0.3) // Elliptical perspective
        
        val moonCenter = Offset(center.x + x.toFloat(), center.y + y.toFloat())
        val isBehind = z < 0

        fun drawMoonAt(mCenter: Offset) {
            val baseMoonColor = Color(0xFFFFF5E1) // Actual moon color
            val shadowColor = Color(0xFF494949)
            val moonRadius = size.width / 7f
            // Base moon cycle logic
            // 7 day cycle; 7+ = full moon. 
            val cycle = streak.coerceIn(0, 7)
            
            drawCircle(
                color = if (cycle >= 7) baseMoonColor else shadowColor,
                radius = moonRadius,
                center = mCenter
            )
            // Phase overlay (approximate crescent drawing)
            if (cycle in 1..6) {
                val phaseOffset = moonRadius * (1f - (cycle / 7f) * 2f)
                drawCircle(
                    color = shadowColor.copy(alpha = 0.9f),
                    radius = moonRadius * 1.05f, 
                    center = mCenter.copy(x = mCenter.x + phaseOffset)
                )
                // Re-draw a bright crescent edge to look like an actual crescent moon!
                drawCircle(
                    color = baseMoonColor,
                    radius = moonRadius,
                    center = mCenter,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            } else if (cycle == 0) {
                 drawCircle(
                    color = Color.White.copy(alpha=0.3f),
                    radius = moonRadius,
                    center = mCenter,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                )
            }
        }

        if (isBehind) {
            drawMoonAt(moonCenter)
            // re-draw Earth on top
            drawCircle(color = Color(0xFF1E88E5), radius = size.width / 4f, center = center)
            drawCircle(color = Color(0xFF43A047), radius = size.width / 6f, center = center.copy(x = center.x - 4f, y = center.y + 4f))
        } else {
            drawMoonAt(moonCenter)
        }
    }
}

// --- Dynamic Customizable Background Canvas ---
@Composable
fun DynamicBackgroundCanvas(selectedStyle: String, isDark: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        when (selectedStyle) {
            "Elegant Dark" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0xFF0C0E16))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF263238).copy(alpha = 0.3f), Color.Transparent),
                            center = center,
                            radius = size.width.coerceAtLeast(size.height) * 0.7f
                        ),
                        radius = size.width.coerceAtLeast(size.height) * 0.7f,
                        center = center
                    )
                }
            }
            "Bento Grid" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0xFF13141A))
                    val spacing = 50.dp.toPx()
                    for (x in 0..(size.width / spacing).toInt()) {
                        drawLine(
                            color = Color(0xFF20222B),
                            start = Offset(x * spacing, 0f),
                            end = Offset(x * spacing, size.height),
                            strokeWidth = 0.5.dp.toPx()
                        )
                    }
                    for (y in 0..(size.height / spacing).toInt()) {
                        drawLine(
                            color = Color(0xFF20222B),
                            start = Offset(0f, y * spacing),
                            end = Offset(size.width, y * spacing),
                            strokeWidth = 0.5.dp.toPx()
                        )
                    }
                }
            }
            "Frosted Glass" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0xFF0F0B1E))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF1744).copy(alpha = 0.18f), Color.Transparent),
                            radius = size.width * 0.6f
                        ),
                        center = Offset(size.width * 0.15f, size.height * 0.25f),
                        radius = size.width * 0.6f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF00E676).copy(alpha = 0.14f), Color.Transparent),
                            radius = size.width * 0.5f
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.75f),
                        radius = size.width * 0.5f
                    )
                }
            }
            "Vibrant Palette" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF311B92), Color(0xFF880E4F), Color(0xFFBF360C))
                        )
                    )
                }
            }
            "Clean Minimalist" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = if (isDark) Color(0xFF0B0B0D) else Color(0xFFFAFAFC))
                }
            }
            else -> { // DEFAULT: "Arabic Lantern" (glowing gold overlapping Rub el Hizb geometric canvas)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = if (isDark) Color(0xFF090A0F) else Color(0xFFFAFBFE))
                    val width = size.width
                    val height = size.height
                    val center = Offset(width / 2, height / 2)
                    val radius = width.coerceAtMost(height) * 0.35f
                    val starPoints = 8
                    val angleStep = 2 * PI / starPoints

                    // Rub el Hizb concentric outlines
                    for (i in 0 until starPoints) {
                        val angle1 = i * angleStep
                        val angle2 = (i + 1) * angleStep
                        val x1 = center.x + radius * cos(angle1).toFloat()
                        val y1 = center.y + radius * sin(angle1).toFloat()
                        val x2 = center.x + radius * cos(angle2).toFloat()
                        val y2 = center.y + radius * sin(angle2).toFloat()

                        drawLine(
                            color = Color(0xFFFFD54F).copy(alpha = 0.18f),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    val subRadius = radius * 0.65f
                    for (j in listOf(0f, PI.toFloat() / 4f)) {
                        val points = List(4) { idx ->
                            val angle = j + idx * (PI / 2)
                            Offset(
                                center.x + subRadius * cos(angle).toFloat(),
                                center.y + subRadius * sin(angle).toFloat()
                            )
                        }
                        for (idx in 0..3) {
                            drawLine(
                                color = Color(0xFFFFC107).copy(alpha = 0.22f),
                                start = points[idx],
                                end = points[(idx + 1) % 4],
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Theme Adaptive Layout Structs & Helpers ---
data class StyleRegime(
    val mainBgColor: Color,
    val cardBgColor: Color,
    val accentColor: Color,
    val textColor: Color,
    val secondaryTextColor: Color,
    val cardBorder: BorderStroke?,
    val cardShape: CornerBasedShape,
    val fontStyleName: String
)

@Composable
fun getStyleRegime(selectedStyle: String, isDark: Boolean): StyleRegime {
    return when (selectedStyle) {
        "Elegant Dark" -> StyleRegime(
            mainBgColor = Color(0xFF0C0E16),
            cardBgColor = Color(0xFF1C1E2C),
            accentColor = Color(0xFF00E5FF), // Neon Cyber Cyan
            textColor = Color.White,
            secondaryTextColor = Color(0xFF90A4AE),
            cardBorder = BorderStroke(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
            cardShape = RoundedCornerShape(8.dp),
            fontStyleName = "Modern"
        )
        "Bento Grid" -> StyleRegime(
            mainBgColor = Color(0xFF13141B),
            cardBgColor = Color(0xFF1D1F28),
            accentColor = Color(0xFFFF9100), // Vibrant Orange
            textColor = Color.White,
            secondaryTextColor = Color(0xFF8194A5),
            cardBorder = BorderStroke(2.dp, Color(0xFF2C2D32)),
            cardShape = RoundedCornerShape(22.dp),
            fontStyleName = "Bento"
        )
        "Frosted Glass" -> StyleRegime(
            mainBgColor = Color(0xFF110729),
            cardBgColor = Color.White.copy(alpha = 0.08f),
            accentColor = Color(0xFFFF4081), // Hot Pink
            textColor = Color.White,
            secondaryTextColor = Color.White.copy(alpha = 0.6f),
            cardBorder = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.25f)),
            cardShape = RoundedCornerShape(16.dp),
            fontStyleName = "Frosted"
        )
        "Vibrant Palette" -> StyleRegime(
            mainBgColor = Color(0xFF1E0C3F),
            cardBgColor = Color(0xFF2E1256).copy(alpha = 0.85f),
            accentColor = Color(0xFFE040FB), // Electric Neon Purple
            textColor = Color.White,
            secondaryTextColor = Color(0xFFFF80AB),
            cardBorder = BorderStroke(2.dp, Color(0xFFE040FB).copy(alpha = 0.6f)),
            cardShape = RoundedCornerShape(18.dp),
            fontStyleName = "Vibrant"
        )
        "Clean Minimalist" -> StyleRegime(
            mainBgColor = if (isDark) Color(0xFF0A0A0C) else Color(0xFFFAFAFC),
            cardBgColor = if (isDark) Color(0xFF121215) else Color(0xFFFFFFFF),
            accentColor = if (isDark) Color.White else Color.Black,
            textColor = if (isDark) Color.White else Color.Black,
            secondaryTextColor = Color.Gray,
            cardBorder = BorderStroke(0.8.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFD1D1D6)),
            cardShape = RoundedCornerShape(0.dp), // Zero rounding for flat paper style
            fontStyleName = "Minimalist"
        )
        else -> { // DEFAULT: "Arabic Lantern" (generalized Lantern Glow)
            StyleRegime(
                mainBgColor = if (isDark) Color(0xFF090A0F) else Color(0xFFFAFBFE),
                cardBgColor = if (isDark) Color(0xFF141622) else Color(0xFFFFFFFF),
                accentColor = Color(0xFFFFC107), // Lantern glowing gold
                textColor = if (isDark) Color.White else Color(0xFF1F2937),
                secondaryTextColor = if (isDark) Color(0xFFFFD54F).copy(alpha = 0.8f) else Color(0xFF4B5563),
                cardBorder = BorderStroke(1.5.dp, Color(0xFFFFC107).copy(alpha = 0.4f)),
                cardShape = RoundedCornerShape(14.dp),
                fontStyleName = "Lantern"
            )
        }
    }
}

@Composable
fun getAdaptiveCardColors(selectedStyle: String, isDark: Boolean): CardColors {
    val regime = getStyleRegime(selectedStyle, isDark)
    return CardDefaults.cardColors(containerColor = regime.cardBgColor)
}

@Composable
fun getAdaptiveCardBorder(selectedStyle: String): BorderStroke? {
    val regime = getStyleRegime(selectedStyle, isDark = true)
    return regime.cardBorder
}

@Composable
fun getAdaptiveShape(selectedStyle: String): CornerBasedShape {
    val regime = getStyleRegime(selectedStyle, isDark = true)
    return regime.cardShape
}

@Composable
fun getAdaptiveCardBorderColor(selectedStyle: String): Color {
    val regime = getStyleRegime(selectedStyle, isDark = true)
    return if (regime.cardBorder != null) regime.accentColor.copy(alpha = 0.4f) else Color.Transparent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizMasterAppContent(viewModel: QuizViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var hasPickedProfileStart by rememberSaveable { mutableStateOf(false) }
    var showProfileChooserInHeader by remember { mutableStateOf(false) }

    // Apply centralized style themes
    QuizMasterTheme(darkTheme = state.darkTheme) {
        val currentSelectedStyle = state.selectedVisualStyle

        if (!hasPickedProfileStart) {
            StartOfAppProfilePrompt(
                viewModel = viewModel,
                state = state,
                onProfileConfirmed = { hasPickedProfileStart = true }
            )
        } else {
            if (showProfileChooserInHeader) {
                ProfileManagerDialog(
                    viewModel = viewModel,
                    state = state,
                    onDismiss = { showProfileChooserInHeader = false }
                )
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MuslimMoonStarIcon(
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "QuizMaster Pro",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (currentSelectedStyle == "Frosted Glass") Color.Transparent else MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                        actions = {
                            // User Profile active chip link
                            val activeP = state.activeProfile
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { showProfileChooserInHeader = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${activeP?.avatarEmoji ?: "🌙"} ${activeP?.username ?: "Challenger"}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Lv.${activeP?.level ?: 1}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }

                            // Theme Toggler
                            IconButton(onClick = { viewModel.toggleTheme() }) {
                                Icon(
                                    imageVector = if (state.darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )

                    // Persistent responsive Navigation bottom strip with Muslim Style Custom Icons
                    NavigationBar(
                        containerColor = if (currentSelectedStyle == "Frosted Glass") Color.Transparent else MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        val activeHome = state.currentScreen == Screen.Home || state.currentScreen == Screen.QuizPlay
                        NavigationBarItem(
                            selected = activeHome,
                            onClick = { viewModel.navigateTo(Screen.Home) },
                            icon = { 
                                IslamicLanternIcon(tint = if (activeHome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            },
                            label = { Text("Quizzes", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navItemColors
                        )
                        
                        val activeAI = state.currentScreen == Screen.AIImport
                        NavigationBarItem(
                            selected = activeAI,
                            onClick = { viewModel.navigateTo(Screen.AIImport) },
                            icon = { 
                                MuslimMoonStarIcon(tint = if (activeAI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            },
                            label = { Text("AI / Import", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navItemColors
                        )
                        
                        val activeLeaderboard = state.currentScreen == Screen.Leaderboard
                        NavigationBarItem(
                            selected = activeLeaderboard,
                            onClick = { viewModel.navigateTo(Screen.Leaderboard) },
                            icon = { 
                                KaabaIcon(tint = if (activeLeaderboard) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            },
                            label = { Text("Leaderboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navItemColors
                        )
                        
                        val activeStats = state.currentScreen == Screen.Statistics
                        NavigationBarItem(
                            selected = activeStats,
                            onClick = { viewModel.navigateTo(Screen.Statistics) },
                            icon = { 
                                MinaretCompassIcon(tint = if (activeStats) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navItemColors
                        )
                        
                        val activeGarbage = state.currentScreen == Screen.GarbageCollection
                        NavigationBarItem(
                            selected = activeGarbage,
                            onClick = { viewModel.navigateTo(Screen.GarbageCollection) },
                            icon = { 
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep, 
                                    contentDescription = "Trash Bin", 
                                    tint = if (activeGarbage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text("Garbage Bin", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = navItemColors
                        )
                    }
                }
            ) { paddingValues ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                val isWideScreen = maxWidth > 600.dp

                Column(modifier = Modifier.fillMaxSize()) {
                    // Friendly interactive tooltip for beginners
                    state.tooltipMessage?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 4.dp,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Tooltip info",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.dismissTooltip() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss Tooltip",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Main App Switchboard according to Selected Screen
                    Box(modifier = Modifier.weight(1f)) {
                        when (state.currentScreen) {
                            Screen.Home -> HomeScreen(viewModel, state, isWideScreen)
                            Screen.QuizPlay -> QuizPlayScreen(viewModel, state, isWideScreen)
                            Screen.AIImport -> AIImportScreen(viewModel, state, isWideScreen)
                            Screen.Leaderboard -> LeaderboardScreen(viewModel, state, isWideScreen)
                            Screen.Statistics -> StatisticsScreen(viewModel, state, isWideScreen)
                            Screen.GarbageCollection -> GarbageCollectionScreen(viewModel, state, isWideScreen)
                        }
                    }
                }
            }
        }
        }
    }
}

// ======================== SCREEN 1: HOME SCREEN ========================
@Composable
fun HomeScreen(viewModel: QuizViewModel, state: QuizUiState, isWideScreen: Boolean) {
    val currentStyle = state.selectedVisualStyle
    val isDark = state.darkTheme

    Box(modifier = Modifier.fillMaxSize()) {
        DynamicBackgroundCanvas(selectedStyle = currentStyle, isDark = isDark, modifier = Modifier.fillMaxSize())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // 1. STYLE SCHEMES & LABS (Horizontal visual slider)
            item {
                Text(
                    text = "DESIGNER STYLE REGIMES",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (currentStyle == "Clean Minimalist") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val visualStylesList = listOf(
                        "Arabic Lantern" to "🌙 Lantern Glow",
                        "Elegant Dark" to "🖤 Elegant Dark",
                        "Bento Grid" to "🍱 Bento Grid",
                        "Frosted Glass" to "❄️ Frosted Glass",
                        "Vibrant Palette" to "🎨 Vibrant Neon",
                        "Clean Minimalist" to "⬜ Clean Minimal"
                    )
                    
                    items(visualStylesList) { (themeName, label) ->
                        val isSelected = currentStyle == themeName
                        val btnRegime = getStyleRegime(themeName, isDark)
                        val activeAccent = btnRegime.accentColor
                        Surface(
                            onClick = { viewModel.selectVisualStyle(themeName) },
                            shape = btnRegime.cardShape,
                            color = if (isSelected) activeAccent else btnRegime.cardBgColor,
                            border = if (isSelected) BorderStroke(2.dp, activeAccent) else btnRegime.cardBorder,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        if (themeName == "Clean Minimalist") {
                                            if (isDark) Color.Black else Color.White
                                        } else {
                                            Color(0xFF0F1221) // High-contrast dark rich navy/black on golden/cyan/pink bg
                                        }
                                    } else {
                                        if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF1F2937)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 2. HERO BANNER SECTION (Adapts color/shape)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = getAdaptiveShape(currentStyle),
                    elevation = CardDefaults.cardElevation(if (currentStyle == "Clean Minimalist") 0.dp else 6.dp),
                    border = getAdaptiveCardBorder(currentStyle),
                    colors = getAdaptiveCardColors(currentStyle, isDark)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_quiz_banner),
                            contentDescription = "Starry Arabic lanterns banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "QuizMaster Pro",
                                    color = Color(0xFFFFC107),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Competitive Job MCQ Portal, Visual Themes, and Exam Preps.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                            AnimatedAttentionMoon(modifier = Modifier.size(52.dp).padding(end = 4.dp))
                        }
                    }
                }
            }

            // 2.5. ACTIVE PROFILE STATS HUD & RESUME STUDY SESSION
            item {
                val activeProf = state.activeProfile
                if (activeProf != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = getAdaptiveShape(currentStyle),
                        border = getAdaptiveCardBorder(currentStyle),
                        colors = getAdaptiveCardColors(currentStyle, isDark),
                        elevation = CardDefaults.cardElevation(if (currentStyle == "Clean Minimalist") 0.dp else 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar circle & Streak System
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    StreakMoonAnimation(
                                        streak = activeProf.currentStreak,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Streak: ${activeProf.currentStreak}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeProf.username,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Level ${activeProf.level}",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = getCountryWithFlag(activeProf.primaryCountry),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                                
                                // Switch Button
                                var showProfileManager by remember { mutableStateOf(false) }
                                Button(
                                    onClick = { showProfileManager = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("manage_profiles_btn")
                                ) {
                                    Text("Switch Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                if (showProfileManager) {
                                    ProfileManagerDialog(
                                        viewModel = viewModel,
                                        state = state,
                                        onDismiss = { showProfileManager = false }
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // LEVEL XP GAUGE BAR
                            val xpNeeded = activeProf.level * 300
                            val xpBaseOfCurrentLevel = (activeProf.level - 1) * 300
                            val xpGainedInCurrentLevel = (activeProf.totalXP - xpBaseOfCurrentLevel).coerceIn(0, 300)
                            val xpProgressPercent = xpGainedInCurrentLevel / 300f
                            
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("XP Progress", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Text("${activeProf.totalXP} / $xpNeeded XP", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { xpProgressPercent },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // CUMULATIVE STATS ROW (Attempts, Success, Failed)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TOTAL TRIES", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Text("${activeProf.totalTries}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("SUCCESSFUL", fontSize = 10.sp, color = EmeraldSuccess.copy(alpha = 0.8f))
                                    Text("${activeProf.succeededQuizzesCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("FAILED", fontSize = 10.sp, color = CrimsonFailure.copy(alpha = 0.8f))
                                    Text("${activeProf.failedQuizzesCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CrimsonFailure)
                                }
                            }
                        }
                    }
                }
            }

            // 2.55 DAILY STREAK REMINDER
            item {
                val activeProf = state.activeProfile
                if (activeProf != null) {
                    val now = System.currentTimeMillis()
                    val dayInMs = 24 * 60 * 60 * 1000L
                    val currentDay = now / dayInMs
                    val lastPlayedDay = activeProf.lastPlayedDate / dayInMs
                    if (currentDay > lastPlayedDay) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = getAdaptiveShape(currentStyle),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha=0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Keep Your Moon Streak Alive!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 14.sp)
                                    Text("Take a quiz today to protect your streak. Don't break the cycle!", color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha=0.8f), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 2.6. RESUME UNFINISHED STUDY SESSION BANNER
            item {
                val activeProf = state.activeProfile
                if (activeProf != null && !activeProf.activeQuizQuestionsJson.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = getAdaptiveShape(currentStyle),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚨", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "UNFINISHED STUDY SESSION DETECTED!",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Resume ${activeProf.activeQuizSubject} (${activeProf.activeQuizDifficulty}) at Question ${(activeProf.activeQuizCurrentIndex) + 1}/10",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Button(
                                    onClick = { viewModel.resumeActiveQuiz() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("resume_quiz_btn")
                                ) {
                                    Text("Resume", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                TextButton(
                                    onClick = { 
                                        viewModel.abandonQuizSession()
                                    }
                                ) {
                                    Text("Abandon", fontSize = 10.sp, color = CrimsonFailure)
                                }
                            }
                        }
                    }
                }
            }

            // 3. GOVERNMENT JOB EXAM SECTOR (Country selection)
            item {
                Text(
                    text = "CIVIL SERVICE & GOVT JOB PORTALS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Horizontal list of countries
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val countriesList = listOf(
                        "Malaysia 🇲🇾" to "Malaysia",
                        "India 🇮🇳" to "India",
                        "Pakistan 🇵🇰" to "Pakistan",
                        "Saudi Arabia 🇸🇦" to "Saudi Arabia",
                        "UAE 🇦🇪" to "UAE",
                        "Egypt 🇪🇬" to "Egypt"
                    )
                    
                    items(countriesList) { (label, rawName) ->
                        val isSelectedCountry = state.selectedCountry == rawName
                        val hasSelectedSubject = state.selectedSubject == "Govt Jobs Prep ($rawName)"
                        val isActive = isSelectedCountry || hasSelectedSubject

                        Surface(
                            onClick = { viewModel.selectCountry(rawName) },
                            shape = getAdaptiveShape(currentStyle),
                            color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else getAdaptiveCardColors(currentStyle, isDark).containerColor,
                            border = BorderStroke(
                                width = if (isActive) 1.8.dp else 1.dp,
                                color = if (isActive) MaterialTheme.colorScheme.primary else getAdaptiveCardBorderColor(currentStyle)
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFF00E676), shape = CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Show active sector dashboard
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = getAdaptiveShape(currentStyle),
                    colors = getAdaptiveCardColors(currentStyle, isDark),
                    border = getAdaptiveCardBorder(currentStyle)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🏛️", fontSize = 24.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Active Sector: ${state.selectedCountry.uppercase()} Admin Exam",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Syllabus covers Constitution, admin regulations, public policy and history.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Button(
                            onClick = { 
                                viewModel.selectSubject("Govt Jobs Prep (${state.selectedCountry})")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), contentColor = MaterialTheme.colorScheme.primary),
                            shape = getAdaptiveShape(currentStyle),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("LOCK IN", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // 4. STANDARD CATEGORIES
            item {
                Text(
                    text = "ACADEMIC STUDY SUBJECTS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }

            // Grid Layout of quiz categories (Bento Grid is represented here gracefully!)
            item {
                val columns = if (isWideScreen) 3 else 2
                
                if (currentStyle == "Bento Grid") {
                    // Unique Bento style dashboard with asymmetrical row layouts
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val chunks = state.uniqueSubjects.chunked(2)
                        chunks.forEachIndexed { idx, pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                pair.forEachIndexed { itemIdx, subj ->
                                    val isSelected = subj == state.selectedSubject
                                    val weight = if (idx % 2 == 0) {
                                        if (itemIdx == 0) 1.2f else 0.8f
                                    } else {
                                        if (itemIdx == 0) 0.8f else 1.2f
                                    }
                                    
                                    Card(
                                        modifier = Modifier
                                            .weight(weight)
                                            .height(if (isSelected) 100.dp else 80.dp)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF2E2F33),
                                                shape = getAdaptiveShape(currentStyle)
                                            ),
                                        colors = getAdaptiveCardColors(currentStyle, isDark),
                                        shape = getAdaptiveShape(currentStyle),
                                        onClick = { viewModel.selectSubject(subj) }
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                            Text(
                                                text = if (subj.startsWith("Govt Jobs Prep")) {
                                                    val country = subj.substringAfter("Govt Jobs Prep ").removeSurrounding("(", ")").trim()
                                                    "🏛️ ${getCountryWithFlag(country)}"
                                                } else {
                                                    when {
                                                        subj.lowercase().contains("science") -> "🧪 $subj"
                                                        subj.lowercase().contains("math") -> "➗ $subj"
                                                        subj.lowercase().contains("computer") -> "💻 $subj"
                                                        subj.lowercase().contains("general") -> "🌍 $subj"
                                                        else -> "📚 $subj"
                                                    }
                                                },
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.align(Alignment.TopStart)
                                            )
                                            
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                                        .align(Alignment.BottomEnd)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    CustomGridSubjectLayout(
                        subjects = state.uniqueSubjects,
                        selectedSubject = state.selectedSubject,
                        columns = columns,
                        selectedStyle = currentStyle,
                        isDark = isDark,
                        onSubjectSelected = { viewModel.selectSubject(it) }
                    )
                }
            }

            // 5. DIFFICULTY SELECT PANEL
            item {
                Text(
                    text = "SELECT DIFFICULTY LEVEL",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Easy", "Medium", "Hard", "Asian").forEach { difficulty ->
                        val isAsianMode = difficulty == "Asian"
                        val isSelected = state.selectedDifficulty == difficulty

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(getAdaptiveShape(currentStyle))
                                .background(
                                    when {
                                        isSelected && isAsianMode -> Color(0xFFFF1744)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> getAdaptiveCardColors(currentStyle, isDark).containerColor
                                    }
                                )
                                .border(
                                    width = if (isSelected) 0.dp else 1.2.dp,
                                    color = if (isSelected) Color.Transparent else getAdaptiveCardBorderColor(currentStyle),
                                    shape = getAdaptiveShape(currentStyle)
                                )
                                .clickable { viewModel.selectDifficulty(difficulty) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = difficulty,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                                if (isAsianMode) {
                                    Text(
                                        "🔥 ROASTS",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.White else Color(0xFFFF1744)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. START PLAY TRIGGER ACTION BOX
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.startQuizSession() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = getAdaptiveShape(currentStyle),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.selectedDifficulty == "Asian") Color(0xFFFF1744) else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(if (currentStyle == "Clean Minimalist") 0.dp else 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IslamicLanternIcon(tint = Color.White)
                        Text(
                            text = if (state.selectedDifficulty == "Asian") "ENTER ASIAN LEVEL TRIAL" else "START STUDY QUIZ CHALLENGE",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomGridSubjectLayout(
    subjects: List<String>,
    selectedSubject: String,
    columns: Int,
    selectedStyle: String,
    isDark: Boolean,
    onSubjectSelected: (String) -> Unit
) {
    val rows = subjects.chunked(columns)
    val regime = getStyleRegime(selectedStyle, isDark)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (selectedStyle == "Bento Grid") 12.dp else 8.dp)
    ) {
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(if (selectedStyle == "Bento Grid") 12.dp else 8.dp)
            ) {
                row.forEachIndexed { itemIndex, subj ->
                    val isSelected = subj == selectedSubject
                    val activeColor = regime.accentColor
                    val cardBg = if (isSelected) activeColor.copy(alpha = 0.2f) else regime.cardBgColor
                    val borderStroke = if (isSelected) {
                        BorderStroke(2.dp, activeColor)
                    } else {
                        regime.cardBorder ?: BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
                    }
                    
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(borderStroke, regime.cardShape),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = regime.cardShape,
                        onClick = { onSubjectSelected(subj) }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(if (selectedStyle == "Clean Minimalist") 10.dp else 12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (selectedStyle == "Clean Minimalist") {
                                val rowIndex = rows.indexOf(row)
                                val indexStr = String.format(java.util.Locale.ROOT, "%02d", rowIndex * columns + itemIndex + 1)
                                Text(
                                    text = "[$indexStr]",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) activeColor else regime.textColor.copy(alpha = 0.6f),
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                val icon = when {
                                    subj.lowercase().contains("malaysia") -> Icons.Default.Public
                                    subj.lowercase().contains("india") -> Icons.Default.Public
                                    subj.lowercase().contains("pakistan") -> Icons.Default.Public
                                    subj.lowercase().contains("saudi") -> Icons.Default.Public
                                    subj.lowercase().contains("uae") -> Icons.Default.Public
                                    subj.lowercase().contains("egypt") -> Icons.Default.Public
                                    subj.lowercase().contains("science") -> Icons.Default.Science
                                    subj.lowercase().contains("math") -> Icons.Default.Functions
                                    subj.lowercase().contains("computer") -> Icons.Default.Computer
                                    subj.lowercase().contains("general") -> Icons.Default.Public
                                    else -> Icons.Default.ImportContacts
                                }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            activeColor.copy(alpha = 0.12f),
                                            shape = if (selectedStyle == "Elegant Dark") RoundedCornerShape(4.dp) else CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Subject Icon",
                                        tint = if (isSelected) activeColor else regime.textColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            val displayTitle = if (subj.startsWith("Govt Jobs Prep")) {
                                val country = subj.substringAfter("Govt Jobs Prep ").removeSurrounding("(", ")").trim()
                                getCountryWithFlag(country)
                            } else {
                                when {
                                    subj.lowercase().contains("science") -> "🧪 $subj"
                                    subj.lowercase().contains("math") -> "➗ $subj"
                                    subj.lowercase().contains("computer") -> "💻 $subj"
                                    subj.lowercase().contains("general") -> "🌍 $subj"
                                    else -> "📚 $subj"
                                }
                            }
                            
                            Text(
                                text = displayTitle,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (isSelected) activeColor else regime.textColor
                            )
                        }
                    }
                }
                if (row.size < columns) {
                    val missing = columns - row.size
                    for (m in 0 until missing) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


// ======================== SCREEN 2: PLAYING QUIZ PANEL ========================
@Composable
fun QuizPlayScreen(viewModel: QuizViewModel, state: QuizUiState, isWideScreen: Boolean) {
    val currentQuestion = state.quizActiveQuestions.getOrNull(state.currentQuestionIndex)

    if (currentQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Questions Available. Wait...", color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Subject + Progress + Timer Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = state.selectedSubject.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Question ${state.currentQuestionIndex + 1} of ${state.quizActiveQuestions.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Question Countdown gauge (Shown for timed modes: Medium, Hard, Asian)
                if (state.timerRemainingSeconds > 0) {
                    val progressFraction = state.timerRemainingSeconds.toFloat() / 
                            com.example.quiz.QuizEngine.getTimeLimitPerQuestion(state.selectedDifficulty).toFloat()
                    
                    val progressColor = when {
                        state.timerRemainingSeconds <= 3 -> Color.Red
                        state.timerRemainingSeconds <= 6 -> Color.Yellow
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Box(
                        modifier = Modifier.size(54.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier.fillMaxSize(),
                            color = progressColor,
                            strokeWidth = 4.dp,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        Text(
                            text = "${state.timerRemainingSeconds}s",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = progressColor
                        )
                    }
                }
            }

            // Horizontal visual Progress bar
            LinearProgressIndicator(
                progress = { (state.currentQuestionIndex.toFloat() / state.quizActiveQuestions.size.toFloat()) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            val isAnsweredOuter = state.isAnswerSelected
            val isCorrectOuter = state.isCorrectAnswer
            
            val outerContainerColor = when {
                isAnsweredOuter && isCorrectOuter -> EmeraldSuccess.copy(alpha = 0.15f)
                isAnsweredOuter && !isCorrectOuter -> CrimsonFailure.copy(alpha = 0.15f)
                else -> Color.Transparent
            }
            
            val outerBorderColor = when {
                isAnsweredOuter && isCorrectOuter -> EmeraldSuccess
                isAnsweredOuter && !isCorrectOuter -> CrimsonFailure
                else -> Color.Transparent
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // to take remaining space
                    .border(width = if (isAnsweredOuter) 2.dp else 0.dp, color = outerBorderColor, shape = RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = outerContainerColor),
                elevation = CardDefaults.cardElevation(if (isAnsweredOuter) 4.dp else 0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(if (isAnsweredOuter) 14.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // MCQ Question Text panel
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.35f, fill = true),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentQuestion.questionText,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }
    
                    // Options selection list
                    Column(
                        modifier = Modifier.weight(0.65f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val options = listOf(
                            "A" to currentQuestion.optionA,
                            "B" to currentQuestion.optionB,
                            "C" to currentQuestion.optionC,
                            "D" to currentQuestion.optionD
                        )
                        
                        options.forEach { (prefix, optionText) ->
                            val isSelected = state.lastSelectedOption == prefix
                            val isCorrect = prefix == currentQuestion.correctAnswer
                            val isAnswered = state.isAnswerSelected
    
                            val showOption = if (isAnswered && isCorrectOuter) {
                                // If selection is correct, all other mcqs disappear
                                isCorrect
                            } else {
                                true
                            }
                            
                            if (showOption) {
                                // Sophisticated option coloring based on prompt rules
                                val cardBgColor = when {
                                    isSelected && isAnswered && isCorrect -> EmeraldSuccess.copy(alpha = 0.25f)
                                    isSelected && isAnswered && !isCorrect -> CrimsonFailure.copy(alpha = 0.25f)
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    isAnswered && isCorrect -> EmeraldSuccess.copy(alpha = 0.8f) // Only correct answer shows green 
                                    isAnswered && !isCorrectOuter -> CrimsonFailure.copy(alpha = 0.1f) // Reddish dim 
                                    else -> MaterialTheme.colorScheme.surface
                                }
        
                                val borderColor = when {
                                    isSelected && isAnswered && isCorrect -> EmeraldSuccess
                                    isSelected && isAnswered && !isCorrect -> CrimsonFailure
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isAnswered && isCorrect -> EmeraldSuccess
                                    isAnswered && !isCorrectOuter -> CrimsonFailure.copy(alpha = 0.3f)
                                    else -> Color.Transparent
                                }
                                
                                val textColor = if (isAnswered && isCorrect) Color.White else MaterialTheme.colorScheme.onSurface
                                val textAlpha = if (isAnswered && !isCorrect && !isCorrectOuter) 0.5f else 1f
        
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f) // Even option distribution
                                        .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                    onClick = { viewModel.submitAnswerSelection(prefix) },
                                    enabled = !state.isAnswerSelected
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Circular Badge for Prefix (A, B, C, D)
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    if (isSelected || (isAnswered && isCorrect)) {
                                                        if (isAnswered && isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.background.copy(alpha = if (isAnswered) 0.5f else 1f)
                                                    },
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = prefix,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected || (isAnswered && isCorrect)) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
                                                fontSize = 14.sp
                                            )
                                        }
        
                                        Spacer(modifier = Modifier.width(16.dp))
        
                                        Text(
                                            text = optionText,
                                            modifier = Modifier.weight(1f),
                                            color = textColor.copy(alpha = textAlpha),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
        
                                        // Status Icons
                                        if (isAnswered) {
                                            if (isCorrect) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = EmeraldSuccess)
                                            } else if (isSelected) {
                                                Icon(Icons.Default.Cancel, contentDescription = "Incorrect", tint = CrimsonFailure)
                                            }
                                        }
                                    } // Close Row
                                } // Close Card
                            } // Close if (showOption)
                        } // Close options.forEach
                    } // Close options Column
                } // Close Column
            } // Close Card
            
            // Next Question Button (moved outside the question Card, but inside the main Column)
            AnimatedVisibility(
                visible = state.isAnswerSelected,
                enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { 50 }) + fadeOut(),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Button(
                    onClick = { viewModel.proceedToNextQuestion() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (state.currentQuestionIndex < state.quizActiveQuestions.size - 1) "Next Question" else "Finish & See Result",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } // Close outer main Column
    } // Close Box
} // Close fun QuizPlayScreen


// ======================== SCREEN 3: DYNAMIC AI MCQS & TXT IMPORT STUDIO ========================
@Composable
fun AIImportScreen(viewModel: QuizViewModel, state: QuizUiState, isWideScreen: Boolean) {
    var isAITab by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Toggle tabs
        item {
            TabRow(selectedTabIndex = if (isAITab) 0 else 1, containerColor = Color.Transparent) {
                Tab(
                    selected = isAITab,
                    onClick = { isAITab = true },
                    text = { Text("Generate via Gemini AI", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
                Tab(
                    selected = !isAITab,
                    onClick = { isAITab = false },
                    text = { Text("Manual TXT File Import", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
            }
        }

        if (isAITab) {
            // GEMINI INTERFACE
            item {
                Text(
                    text = "AI GENERATION CONSOLE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Create completely personalized, high-quality challenging trivia questions instantly on any concept or study subjects in real time.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            item {
                OutlinedTextField(
                    value = state.aiTopic,
                    onValueChange = { viewModel.updateAiTopic(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Topic of Study (e.g. quantum physics, organic chemistry, medieval dates)") },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text("Select Generated Difficulty:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Easy", "Medium", "Hard", "Asian").forEach { level ->
                        val isSel = state.aiDifficultySelected == level
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateAiDifficulty(level) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // OpenAI / Custom manual API endpoints links (allows manual links)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Custom manual AI Endpoints (optional link selector)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = state.manualAiUrlLink,
                            onValueChange = { viewModel.updateManualAiUrl(it) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 12.sp),
                            label = { Text("Base URL integration") },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.generateAiMcqs() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp), // Increased slightly for progress bar
                    enabled = !state.aiGenerating,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.aiGenerating) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Crafting questions... ${state.aiGenerationProgress}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { state.aiGenerationProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Magic")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GENERATE AI QUESTIONS WITH GEMINI")
                    }
                }
            }

            if (state.aiError != null) {
                item {
                    Surface(
                        color = Color(0xFF2D1619),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.aiError,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (state.aiSuccess != null) {
                item {
                    Surface(
                        color = Color(0xFF14291B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.aiSuccess,
                            color = Color(0xFF69F0AE),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

        } else {
            // MANUAL TXT FILE LOADER
            item {
                Text(
                    text = "MANUAL TXT LOADER",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Write or paste multiple choice questions in TXT syntax block structure. Click 'LOAD TEMPLATE' standard formula to check the exact format.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.loadTxtTemplate() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FileCopy, contentDescription = "Template")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LOAD TXT FORMULA", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.importCustomTxtQuiz() },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("IMPORT MCQS", fontSize = 12.sp)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = state.rawTxtToImport,
                    onValueChange = { viewModel.updateRawTxt(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    placeholder = { Text("Subject: My Topic\nDifficulty: Easy\n\nQ: Question?\nA: Option\nB: Option\nC: Option\nD: Option\nCorrect: B") },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (state.txtImportError != null) {
                item {
                    Surface(
                        color = Color(0xFF2D1619),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.txtImportError,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (state.txtImportSuccess != null) {
                item {
                    Surface(
                        color = Color(0xFF14291B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.txtImportSuccess,
                            color = Color(0xFF69F0AE),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}


// ======================== SCREEN 4: REAL-TIME LEADERBOARD ARENA ========================
@Composable
fun LeaderboardScreen(viewModel: QuizViewModel, state: QuizUiState, isWideScreen: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        DynamicBackgroundCanvas(selectedStyle = state.selectedVisualStyle, isDark = state.darkTheme, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "LEADERBOARD ARENA",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Compete live with users globally! Ranks shift in real-time as other active simulated online players perform study sessions.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Highscore Top-3 Podium summary boxes
            val sortedList = state.leaderboardPlayers.sortedByDescending { it.xp }
            val top3 = sortedList.take(3)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Number 2
                PodiumBox(
                    player = top3.getOrNull(1),
                    rankText = "2nd",
                    rankColor = Color(0xFFB0BEC5),
                    modifier = Modifier.weight(1f)
                )
                // Number 1 (Middle and taller)
                PodiumBox(
                    player = top3.getOrNull(0),
                    rankText = "Champion",
                    rankColor = Color(0xFFFFD54F),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(140.dp)
                )
                // Number 3
                PodiumBox(
                    player = top3.getOrNull(2),
                    rankText = "3rd",
                    rankColor = Color(0xFFFFAB91),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Scrollable rank list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedList.drop(3)) { player ->
                    val isYou = player.isUser
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isYou) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.3.dp,
                                color = if (isYou) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${sortedList.indexOf(player) + 1}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.width(28.dp)
                        )

                        Text(
                            text = player.avatarEmoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = player.name,
                                    fontWeight = if (isYou) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isYou) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "YOU",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp,
                                        color = Color.White,
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "Country: ${player.countryCode}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Text(
                            text = "${player.xp} XP",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumBox(
    player: LeaderboardPlayer?,
    rankText: String,
    rankColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, rankColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(rankColor.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player?.avatarEmoji ?: "👤",
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = player?.name ?: "Challenger",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${player?.xp ?: 0} XP",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = rankColor,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = rankText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}


// ======================== SCREEN 5: USER STATISTICS & USER PROFILE TRACKING ========================
@Composable
fun StatisticsScreen(viewModel: QuizViewModel, state: QuizUiState, isWideScreen: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                "USER STATE & METRICS",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (state.lastMockResultText != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.lastMockResultText.contains("PASSED")) EmeraldSuccess.copy(alpha = 0.2f) else CrimsonFailure.copy(alpha = 0.2f)
                    ),
                    border = BorderStroke(2.dp, if (state.lastMockResultText.contains("PASSED")) EmeraldSuccess else CrimsonFailure)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LATEST MOCK EXAM RESULT",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.lastMockResultText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = if (state.lastMockResultText.contains("PASSED")) EmeraldSuccess else CrimsonFailure
                        )
                    }
                }
            }
        }

        // Progression Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎮", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Challenger Neo",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Level ${state.userStats.level} Accomplished",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Level XP Indicator Bar
                    val nextLevelTargetXp = state.userStats.level * 300
                    val currentLevelBaseXp = (state.userStats.level - 1) * 300
                    val levelGainedXp = state.userStats.totalXP - currentLevelBaseXp
                    val progressFraction = (levelGainedXp.toFloat() / 300f).coerceIn(0f, 1f)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("XP: ${state.userStats.totalXP} TOTAL", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("${(progressFraction * 100).toInt()}% towards Lev.${state.userStats.level + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Circular Stats Gauges Block
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Correct Count Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("CORRECT MCQS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.userStats.correctAnswersCount}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = EmeraldSuccess
                        )
                    }
                }

                // Wrong Count Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("WRONG ANSWERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonFailure)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.userStats.wrongAnswersCount}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = CrimsonFailure
                        )
                    }
                }
            }
        }

        // History list items
        item {
            Text(
                "STUDY SESSION LOGS",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
        }

        if (state.logsList.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No quizzes logged yet. Complete study sessions to track records!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
        } else {
            items(state.logsList) { log ->
                val dateStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                val percentage = ((log.score.toFloat() / log.totalQuestions.toFloat()) * 100).toInt()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${log.subject} (${log.difficulty})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$dateStr • +${log.xpGained} XP",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${log.score}/${log.totalQuestions}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (percentage >= 70) EmeraldSuccess else if (percentage >= 40) Color.Yellow else CrimsonFailure
                        )
                        Text(
                            text = "$percentage% Ratio",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = { viewModel.softDeleteLogById(log.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Trash log",
                            tint = CrimsonFailure.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}


// ======================== SCREEN 6: GARBAGE COLLECTION DASHBOARD ========================
@Composable
fun GarbageCollectionScreen(viewModel: QuizViewModel, state: QuizUiState, isWideScreen: Boolean) {
    var isLogsBinSelected by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                "QUIZMASTER GARBAGE COLLECTOR",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Clean drafts, custom uploads, or history session logs. Items are soft deleted first and can be purged from storage permanently or restored.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Toggles between Question Bin and Logs Bin
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isLogsBinSelected = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLogsBinSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (!isLogsBinSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Deletes Quizzes (${state.trashedQuestions.size})")
                }

                Button(
                    onClick = { isLogsBinSelected = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLogsBinSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (isLogsBinSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Deleted Logs (${state.trashedLogs.size})")
                }
            }
        }

        // Universal Sweep Button (Runs Garbage Collection)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF220C11), shape = RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("RECLAIM SYSTEM Speicher Cache", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text("Irreversibly wipe out trashed entities completely.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (isLogsBinSelected) {
                            viewModel.emptyLogsTrashBin()
                        } else {
                            viewModel.emptyQuestionTrashBin()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonFailure),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "GC Run")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TRIGGER GC", fontSize = 11.sp)
                }
            }
        }

        // List contents of respective Bin
        if (!isLogsBinSelected) {
            // Deleted Questions Bin
            if (state.trashedQuestions.isEmpty()) {
                item {
                    EmptyBinPlaceholder(text = "Quiz questions trash is completely empty! No soft deleted drafts discovered.")
                }
            } else {
                items(state.trashedQuestions) { q ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "${q.subject} (${q.difficulty})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Restore
                                    IconButton(
                                        onClick = { viewModel.restoreQuestionById(q.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = "Restore", tint = EmeraldSuccess)
                                    }
                                    // Purge
                                    IconButton(
                                        onClick = { viewModel.permanentlyPurgeQuestionById(q.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteForever, contentDescription = "Purge", tint = CrimsonFailure)
                                    }
                                }
                            }
                            Text(q.questionText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        } else {
            // Deleted Logs Bin
            if (state.trashedLogs.isEmpty()) {
                item {
                    EmptyBinPlaceholder(text = "Study sessions logs trash is completely empty!")
                }
            } else {
                items(state.trashedLogs) { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${log.subject} (${log.difficulty})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text("Score: ${log.score}/${log.totalQuestions}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { viewModel.restoreLogById(log.id) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Restore, contentDescription = "Restore", tint = EmeraldSuccess)
                            }
                            IconButton(onClick = { viewModel.permanentlyPurgeLogById(log.id) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.DeleteForever, contentDescription = "Purge", tint = CrimsonFailure)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyBinPlaceholder(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.RestoreFromTrash,
                contentDescription = "Empty Trash",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagerDialog(
    viewModel: QuizViewModel,
    state: QuizUiState,
    onDismiss: () -> Unit
) {
    var isCreatingNew by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("🌙") }
    var selectedCountry by remember { mutableStateOf("Malaysia") }

    val countriesList = listOf("Malaysia", "India", "Pakistan", "Saudi Arabia", "UAE", "Egypt")
    val avatarsList = listOf("🌙", "👑", "🚀", "🐼", "🎮", "🧠", "🦁", "⭐", "🐪", "🕌", "🦅", "⚔️", "🛡️")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isCreatingNew) "CREATE SCHOLASTIC PROFILE" else "SELECT ACTIVE PROFILE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isCreatingNew) {
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { if (it.length <= 15) inputName = it },
                        label = { Text("Profile Nickname") },
                        placeholder = { Text("Challenger Neo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text(
                        text = "CHOOSE AVATAR EMOJI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(avatarsList) { emoji ->
                            val isChosen = selectedAvatar == emoji
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }

                    Text(
                        text = "CHOOSE PRIMARY EXAM PREP COUNTRY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(countriesList) { country ->
                            val isChosen = selectedCountry == country
                            Surface(
                                onClick = { selectedCountry = country },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                border = if (isChosen) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Text(
                                    text = getCountryWithFlag(country),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.allProfiles) { profile ->
                            val isActive = profile.id == state.activeProfile?.id
                            Card(
                                onClick = {
                                    viewModel.selectProfile(profile.id)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                border = if (isActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(profile.avatarEmoji, fontSize = 20.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = profile.username,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isActive) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Active",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Level ${profile.level} • ${profile.totalXP} XP • ${getCountryWithFlag(profile.primaryCountry)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "Tries: ${profile.totalTries} | Success: ${profile.succeededQuizzesCount} | Fail: ${profile.failedQuizzesCount}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }

                                    if (state.allProfiles.size > 1 && !isActive) {
                                        IconButton(
                                            onClick = { viewModel.deleteProfile(profile.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Profile",
                                                tint = CrimsonFailure,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isCreatingNew) {
                    TextButton(onClick = { isCreatingNew = false }) {
                        Text("BACK")
                    }
                    Button(
                        onClick = {
                            if (inputName.isNotBlank()) {
                                viewModel.createProfile(inputName.trim(), selectedAvatar, selectedCountry)
                                isCreatingNew = false
                                inputName = ""
                            }
                        },
                        enabled = inputName.isNotBlank(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SAVE")
                    }
                } else {
                    Button(
                        onClick = { isCreatingNew = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Add")
                    }
                    TextButton(onClick = onDismiss) {
                        Text("CLOSE")
                    }
                }
            }
        }
    )
}

@Composable
fun StartOfAppProfilePrompt(
    viewModel: QuizViewModel,
    state: QuizUiState,
    onProfileConfirmed: () -> Unit
) {
    val currentStyle = state.selectedVisualStyle
    val isDark = state.darkTheme

    var showCreatorForm by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("🌙") }
    var selectedCountry by remember { mutableStateOf("Malaysia") }

    val countriesList = listOf("Malaysia", "India", "Pakistan", "Saudi Arabia", "UAE", "Egypt")
    val avatarsList = listOf("🌙", "👑", "🚀", "🐼", "🎮", "🧠", "🦁", "⭐", "🐪", "🕌", "🦅", "⚔️", "🛡️")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070913)),
        contentAlignment = Alignment.Center
    ) {
        DynamicBackgroundCanvas(selectedStyle = "Arabic Lantern", isDark = true, modifier = Modifier.fillMaxSize())

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141624)),
            border = BorderStroke(1.5.dp, Color(0xFFFFC107).copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MuslimMoonStarIcon(modifier = Modifier.size(48.dp), tint = Color(0xFFFFC107))

                Text(
                    text = "QUIZMASTER PRO",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFC107),
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (showCreatorForm) "CREATE YOUR CHALLENGER PROFILE" else "SELECT ACTIVE PROFILE TO START",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                if (showCreatorForm) {
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { if (it.length <= 15) inputName = it },
                        label = { Text("Profile Nickname", color = Color(0xFFFFD54F)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xFFFFC107).copy(alpha = 0.5f),
                            focusedLabelColor = Color(0xFFFFD54F),
                            unfocusedLabelColor = Color(0xFFFFD54F).copy(alpha = 0.7f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("splash_profile_name"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "CHOOSE AVATAR EMOJI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(avatarsList) { emoji ->
                                val isChosen = selectedAvatar == emoji
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            if (isChosen) Color(0xFFFFC107) else Color(0xFF23253A),
                                            shape = CircleShape
                                        )
                                        .clickable { selectedAvatar = emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "CIVIL SERVICE EXAM PREP COUNTRY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(countriesList) { country ->
                                val isChosen = selectedCountry == country
                                Surface(
                                    onClick = { selectedCountry = country },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isChosen) Color(0xFFFFC107) else Color(0xFF23253A),
                                ) {
                                    Text(
                                        text = getCountryWithFlag(country),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChosen) Color(0xFF141624) else Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (inputName.isNotBlank()) {
                                viewModel.createProfile(inputName.trim(), selectedAvatar, selectedCountry)
                                onProfileConfirmed()
                            }
                        },
                        enabled = inputName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107),
                            contentColor = Color(0xFF141624),
                            disabledContainerColor = Color(0xFFFFC107).copy(alpha = 0.3f),
                            disabledContentColor = Color.White.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("launch_splash_profile_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("LAUNCH MY PROFILE ➔", fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = { showCreatorForm = false }) {
                        Text("CHOOSE AN EXISTING PROFILE", color = Color(0xFFFFD54F))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(state.allProfiles) { profile ->
                            val isFocused = state.activeProfile?.id == profile.id
                            Card(
                                onClick = {
                                    viewModel.selectProfile(profile.id)
                                    onProfileConfirmed()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isFocused) Color(0xFF2E2D1F) else Color(0xFF1F213A)
                                ),
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isFocused) Color(0xFFFFC107) else Color.Transparent
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF2A2C49), shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(profile.avatarEmoji, fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            profile.username,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "Lv.${profile.level} • ${profile.totalXP} XP • ${getCountryWithFlag(profile.primaryCountry)}",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Select",
                                        tint = Color(0xFFFFC107).copy(alpha = 0.8f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = { showCreatorForm = true },
                        border = BorderStroke(1.2.dp, Color(0xFFFFC107)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFC107)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CREATE A BRAND NEW PROFILE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
