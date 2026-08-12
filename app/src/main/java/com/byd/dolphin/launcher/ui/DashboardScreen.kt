package com.byd.dolphin.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byd.dolphin.launcher.data.VehicleData

// Dudu-inspired dark palette
private val Bg = Color(0xFF0B0E14)
private val CardBg = Color(0xFF151A22)
private val Accent = Color(0xFF3D8BFF)
private val AccentGreen = Color(0xFF2EE59D)
private val TextPrimary = Color(0xFFF2F5FA)
private val TextMuted = Color(0xFF8B93A7)

@Composable
fun DashboardScreen(
    data: VehicleData,
    onOpenApps: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF0D1118), Bg, Color(0xFF0A0C10)))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "BYD Dolphin",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        data.gear?.let { "Số $it  ·  DiLink" } ?: "DiLink",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TopActionChip(Icons.Default.Apps, "Ứng dụng", onOpenApps)
                    TopActionChip(Icons.Default.Settings, "Cài đặt", onOpenSettings)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Main content
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Car + speed
                Card(
                    modifier = Modifier
                        .weight(1.4f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        DolphinCarAnimation(
                            speedKmh = data.speedKmh ?: 0f,
                            gear = data.gear,
                            isCharging = data.isCharging,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Speed overlay
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Text(
                                data.speedKmh?.let { "%.0f".format(it) } ?: "--",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 56.sp
                            )
                            Text("km/h", color = Color.White.copy(0.75f), fontSize = 14.sp)
                        }
                        // Gear badge
                        data.gear?.let { g ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(0.35f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(g, color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Metrics column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Pin",
                        value = data.socPercent?.let { "%.0f".format(it) } ?: "--",
                        unit = "%",
                        icon = Icons.Default.BatteryChargingFull,
                        accent = batteryColor(data.socPercent),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Quãng đường",
                        value = data.rangeKm?.let { "%.0f".format(it) } ?: "--",
                        unit = "km",
                        icon = Icons.Default.Speed,
                        accent = Accent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Công suất",
                        value = data.powerKw?.let { "%.1f".format(it) } ?: "--",
                        unit = "kW",
                        icon = Icons.Default.Bolt,
                        accent = Color(0xFFFFB020),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // TPMS row
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Áp suất lốp", color = TextMuted, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TpmsItem("Trước trái", data.tpmsFl)
                        TpmsItem("Trước phải", data.tpmsFr)
                        TpmsItem("Sau trái", data.tpmsRl)
                        TpmsItem("Sau phải", data.tpmsRr)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Bottom nav — lớn, rõ, kiểu Dudu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardBg)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Default.Apps,
                    label = "Ứng dụng",
                    onClick = onOpenApps,
                    modifier = Modifier.weight(1f)
                )
                BottomNavItem(
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                    selected = true,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                BottomNavItem(
                    icon = Icons.Default.Settings,
                    label = "Cài đặt",
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TopActionChip(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = label, tint = TextPrimary, modifier = Modifier.size(20.dp))
        Text(label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val tint = if (selected) Accent else TextMuted
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(26.dp))
        Spacer(Modifier.height(2.dp))
        Text(label, color = tint, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = TextMuted, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Text(unit, color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun TpmsItem(label: String, kpa: Float?) {
    val color = when {
        kpa == null -> TextMuted
        kpa < 200f || kpa > 280f -> Color(0xFFFF5A5A)
        else -> AccentGreen
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextMuted, fontSize = 12.sp)
        Text(
            kpa?.let { "%.0f".format(it) } ?: "--",
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text("kPa", color = TextMuted, fontSize = 11.sp)
    }
}

private fun batteryColor(soc: Float?): Color = when {
    soc == null -> TextMuted
    soc < 20f -> Color(0xFFFF5A5A)
    soc < 40f -> Color(0xFFFFB020)
    else -> AccentGreen
}
