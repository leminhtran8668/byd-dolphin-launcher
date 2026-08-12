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
import kotlin.math.cos
import kotlin.math.sin

/**
 * 2.5D BYD Dolphin-inspired hatch animation.
 * Proportions: short nose, tall rear hatch, continuous LED signature.
 * (True glTF 3D needs SceneView + model asset — heavier on DiLink.)
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
                durationMillis = if (speedKmh > 5f) 420 else 1400,
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

    Canvas(modifier = modifier.fillMaxSize().padding(8.dp)) {
        val w = size.width
        val h = size.height

        // Dark cinematic background (Dudu-like)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1A2332), Color(0xFF0E141C), Color(0xFF0A0E14)),
                startY = 0f,
                endY = h
            )
        )

        // Soft horizon glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2A3A55).copy(0.4f), Color.Transparent),
                startY = h * 0.35f,
                endY = h * 0.55f
            )
        )

        // Road
        val roadTop = h * 0.58f
        val roadH = h * 0.34f
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF2A3038), Color(0xFF1A1E24))
            ),
            topLeft = Offset(0f, roadTop),
            size = Size(w, roadH)
        )

        // Lane dashes
        val dashW = w * 0.07f
        val gap = w * 0.05f
        var x = -dashW + (roadPhase * (dashW + gap))
        while (x < w) {
            drawRoundRect(
                color = Color(0xFFE8C547).copy(0.9f),
                topLeft = Offset(x, roadTop + roadH * 0.48f),
                size = Size(dashW, 7f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            x += dashW + gap
        }
        drawLine(Color.White.copy(0.5f), Offset(0f, roadTop + 3f), Offset(w, roadTop + 3f), 3f)
        drawLine(Color.White.copy(0.5f), Offset(0f, roadTop + roadH - 3f), Offset(w, roadTop + roadH - 3f), 3f)

        // --- BYD Dolphin body (side view, hatch) ---
        val carW = w * 0.58f
        val carH = h * 0.30f
        val cx = w * 0.48f
        val cy = roadTop + roadH * 0.28f + bounce * 2.5f

        // Shadow
        drawOval(
            Color.Black.copy(0.35f),
            topLeft = Offset(cx - carW * 0.46f, cy + carH * 0.40f),
            size = Size(carW * 0.95f, carH * 0.16f)
        )

        val bodyBlue = Color(0xFF3B9EFF)
        val bodyDeep = Color(0xFF1E6FD9)
        val glass = Color(0xFF9AD4FF).copy(0.75f)
        val black = Color(0xFF1A1A1A)

        // Main body path — Dolphin: short hood, rising beltline, tall rear hatch
        val body = Path().apply {
            // Front bumper
            moveTo(cx - carW * 0.48f, cy + carH * 0.22f)
            quadraticBezierTo(cx - carW * 0.52f, cy + carH * 0.32f, cx - carW * 0.46f, cy + carH * 0.42f)
            // Rockers
            lineTo(cx + carW * 0.40f, cy + carH * 0.42f)
            // Rear bumper up
            quadraticBezierTo(cx + carW * 0.48f, cy + carH * 0.40f, cx + carW * 0.47f, cy + carH * 0.18f)
            // Rear hatch top (tall)
            quadraticBezierTo(cx + carW * 0.46f, cy - carH * 0.08f, cx + carW * 0.28f, cy - carH * 0.18f)
            // Roof
            quadraticBezierTo(cx + carW * 0.05f, cy - carH * 0.22f, cx - carW * 0.18f, cy - carH * 0.12f)
            // A-pillar / hood
            quadraticBezierTo(cx - carW * 0.32f, cy - carH * 0.02f, cx - carW * 0.42f, cy + carH * 0.10f)
            quadraticBezierTo(cx - carW * 0.48f, cy + carH * 0.14f, cx - carW * 0.48f, cy + carH * 0.22f)
            close()
        }
        drawPath(
            body,
            brush = Brush.verticalGradient(listOf(bodyBlue, bodyDeep), startY = cy - carH * 0.2f, endY = cy + carH * 0.42f)
        )
        drawPath(body, Color.White.copy(0.12f), style = Stroke(width = 2f))

        // Greenhouse / windows
        val windows = Path().apply {
            moveTo(cx - carW * 0.30f, cy + carH * 0.02f)
            lineTo(cx - carW * 0.08f, cy - carH * 0.10f)
            lineTo(cx + carW * 0.22f, cy - carH * 0.14f)
            lineTo(cx + carW * 0.36f, cy - carH * 0.02f)
            lineTo(cx + carW * 0.34f, cy + carH * 0.10f)
            lineTo(cx - carW * 0.28f, cy + carH * 0.12f)
            close()
        }
        drawPath(windows, glass)
        drawPath(windows, Color.White.copy(0.2f), style = Stroke(1.5f))

        // Character line
        drawLine(
            Color.White.copy(0.25f),
            Offset(cx - carW * 0.40f, cy + carH * 0.18f),
            Offset(cx + carW * 0.38f, cy + carH * 0.16f),
            strokeWidth = 2.5f
        )

        // Headlight (Dolphin vertical-ish signature)
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFB3E5FC))),
            topLeft = Offset(cx - carW * 0.49f, cy + carH * 0.06f),
            size = Size(carW * 0.055f, carH * 0.16f),
            cornerRadius = CornerRadius(5f, 5f)
        )
        // DRL strip
        drawRoundRect(
            Color(0xFFE3F2FD),
            topLeft = Offset(cx - carW * 0.48f, cy + carH * 0.20f),
            size = Size(carW * 0.09f, 3.5f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Continuous rear light bar (Dolphin signature)
        val brakeOn = gear == "R" || speedKmh < 2.5f
        drawRoundRect(
            color = if (brakeOn) Color(0xFFFF1744) else Color(0xFFE53935),
            topLeft = Offset(cx + carW * 0.30f, cy - carH * 0.04f),
            size = Size(carW * 0.16f, carH * 0.07f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Charging pulse
        if (isCharging) {
            drawCircle(Color(0xFF00E676), 12f, Offset(cx + carW * 0.20f, cy - carH * 0.22f))
            drawCircle(Color(0xFF00E676).copy(0.3f), 20f, Offset(cx + carW * 0.20f, cy - carH * 0.22f))
        }

        // Wheels
        fun drawWheel(wx: Float, wy: Float, r: Float) {
            drawCircle(black, r, Offset(wx, wy))
            drawCircle(Color(0xFF37474F), r * 0.68f, Offset(wx, wy))
            rotate(wheelRot, Offset(wx, wy)) {
                for (i in 0 until 5) {
                    val a = i * 72f * (PI / 180f).toFloat()
                    drawCircle(
                        Color(0xFF90A4AE),
                        r * 0.11f,
                        Offset(wx + sin(a) * r * 0.38f, wy - cos(a) * r * 0.38f)
                    )
                }
            }
            drawCircle(Color(0xFF607D8B), r * 0.16f, Offset(wx, wy))
            drawCircle(Color.White.copy(0.15f), r, Offset(wx, wy), style = Stroke(2f))
        }
        val wheelR = carH * 0.20f
        drawWheel(cx - carW * 0.26f, cy + carH * 0.42f, wheelR)
        drawWheel(cx + carW * 0.26f, cy + carH * 0.42f, wheelR)
    }
}
