package com.byd.dolphin.launcher.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * Polished 2.5D-style driving animation of a BYD Dolphin-like hatchback.
 * Body proportions approximate the real Dolphin (short overhangs, rounded roof).
 * Speed drives road scroll + subtle body pitch; gear affects brake lights.
 */
@Composable
fun DolphinCarAnimation(
    speedKmh: Float,
    gear: String? = "D",
    isCharging: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "drive")

    val roadPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when {
                    speedKmh < 1f -> 8000
                    speedKmh < 30f -> 2500
                    speedKmh < 80f -> 1400
                    else -> 900
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "road"
    )

    val bounce by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (speedKmh > 5f) 400 else 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val wheelRot by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when {
                    speedKmh < 1f -> 4000
                    else -> (1200f / (speedKmh / 20f + 1f)).toInt().coerceIn(200, 3000)
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wheel"
    )

    Canvas(modifier = modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height

        // Sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF87CEEB), Color(0xFFE3F2FD), Color(0xFFC8E6C9)),
                startY = 0f,
                endY = h * 0.55f
            )
        )

        // Ground
        drawRect(
            color = Color(0xFF4CAF50).copy(alpha = 0.35f),
            topLeft = Offset(0f, h * 0.55f),
            size = Size(w, h * 0.45f)
        )

        // Road
        val roadTop = h * 0.58f
        val roadH = h * 0.32f
        drawRect(Color(0xFF37474F), topLeft = Offset(0f, roadTop), size = Size(w, roadH))

        // Lane dashes (scrolling)
        val dashW = w * 0.08f
        val gap = w * 0.06f
        var x = -dashW + (roadPhase * (dashW + gap))
        while (x < w) {
            drawRoundRect(
                color = Color(0xFFFFF176),
                topLeft = Offset(x, roadTop + roadH * 0.45f),
                size = Size(dashW, 8f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            x += dashW + gap
        }

        // Road edges
        drawLine(Color.White, Offset(0f, roadTop + 4f), Offset(w, roadTop + 4f), strokeWidth = 4f)
        drawLine(Color.White, Offset(0f, roadTop + roadH - 4f), Offset(w, roadTop + roadH - 4f), strokeWidth = 4f)

        // Car placement
        val carScale = 0.85f
        val carW = w * 0.55f * carScale
        val carH = h * 0.28f * carScale
        val cx = w * 0.5f
        val cy = roadTop + roadH * 0.35f + bounce * 3f

        // Shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(cx - carW * 0.48f, cy + carH * 0.38f),
            size = Size(carW * 0.96f, carH * 0.18f)
        )

        // --- Body (Dolphin-like hatch proportions) ---
        val bodyColor = Color(0xFF42A5F5)      // signature blue-ish
        val darkBody = Color(0xFF1565C0)
        val glass = Color(0xFF81D4FA).copy(alpha = 0.85f)
        val black = Color(0xFF212121)

        // Lower body
        val bodyPath = Path().apply {
            moveTo(cx - carW * 0.48f, cy + carH * 0.15f)
            // front bumper
            quadraticBezierTo(cx - carW * 0.50f, cy + carH * 0.35f, cx - carW * 0.45f, cy + carH * 0.42f)
            // bottom
            lineTo(cx + carW * 0.42f, cy + carH * 0.42f)
            // rear
            quadraticBezierTo(cx + carW * 0.50f, cy + carH * 0.35f, cx + carW * 0.48f, cy + carH * 0.12f)
            // roof rear (hatch)
            quadraticBezierTo(cx + carW * 0.40f, cy - carH * 0.22f, cx + carW * 0.15f, cy - carH * 0.28f)
            // roof
            lineTo(cx - carW * 0.12f, cy - carH * 0.28f)
            // windshield
            quadraticBezierTo(cx - carW * 0.28f, cy - carH * 0.22f, cx - carW * 0.38f, cy + carH * 0.02f)
            // hood
            quadraticBezierTo(cx - carW * 0.46f, cy + carH * 0.05f, cx - carW * 0.48f, cy + carH * 0.15f)
            close()
        }
        drawPath(bodyPath, bodyColor)

        // Side window / glass belt
        val glassPath = Path().apply {
            moveTo(cx - carW * 0.30f, cy - carH * 0.02f)
            lineTo(cx - carW * 0.10f, cy - carH * 0.22f)
            lineTo(cx + carW * 0.22f, cy - carH * 0.22f)
            lineTo(cx + carW * 0.36f, cy - carH * 0.02f)
            close()
        }
        drawPath(glassPath, glass)

        // Hood highlight
        drawPath(
            Path().apply {
                moveTo(cx - carW * 0.45f, cy + carH * 0.12f)
                quadraticBezierTo(cx - carW * 0.30f, cy + carH * 0.02f, cx - carW * 0.12f, cy + carH * 0.08f)
                lineTo(cx - carW * 0.12f, cy + carH * 0.18f)
                quadraticBezierTo(cx - carW * 0.30f, cy + carH * 0.12f, cx - carW * 0.45f, cy + carH * 0.20f)
                close()
            },
            darkBody.copy(alpha = 0.35f)
        )

        // Headlight
        drawRoundRect(
            color = Color(0xFFFFFDE7),
            topLeft = Offset(cx - carW * 0.48f, cy + carH * 0.08f),
            size = Size(carW * 0.08f, carH * 0.12f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Daytime running light strip
        drawRoundRect(
            color = Color(0xFFE3F2FD),
            topLeft = Offset(cx - carW * 0.47f, cy + carH * 0.18f),
            size = Size(carW * 0.06f, 4f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Tail light
        val brakeOn = gear == "R" || speedKmh < 3f
        drawRoundRect(
            color = if (brakeOn) Color(0xFFFF1744) else Color(0xFFC62828),
            topLeft = Offset(cx + carW * 0.42f, cy + carH * 0.05f),
            size = Size(carW * 0.06f, carH * 0.14f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Charging indicator
        if (isCharging) {
            drawCircle(
                color = Color(0xFF00E676),
                radius = 10f,
                center = Offset(cx + carW * 0.35f, cy - carH * 0.05f)
            )
        }

        // Wheels
        fun drawWheel(wx: Float, wy: Float, radius: Float) {
            drawCircle(black, radius, Offset(wx, wy))
            drawCircle(Color(0xFF455A64), radius * 0.62f, Offset(wx, wy))
            rotate(wheelRot, Offset(wx, wy)) {
                for (i in 0 until 5) {
                    val angle = i * 72f * (PI / 180f).toFloat()
                    val ix = wx + sin(angle) * radius * 0.35f
                    val iy = wy - kotlin.math.cos(angle) * radius * 0.35f
                    drawCircle(Color(0xFF90A4AE), radius * 0.12f, Offset(ix, iy))
                }
            }
            drawCircle(Color(0xFF78909C), radius * 0.18f, Offset(wx, wy))
        }

        val wheelR = carH * 0.22f
        drawWheel(cx - carW * 0.28f, cy + carH * 0.42f, wheelR)
        drawWheel(cx + carW * 0.28f, cy + carH * 0.42f, wheelR)

        // Speed text badge
        // (drawn outside Canvas in parent if needed)
    }
}
