package com.byd.dolphin.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byd.dolphin.launcher.data.VehicleData

@Composable
fun DashboardScreen(
    data: VehicleData,
    onOpenApps: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "BYD Dolphin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    data.gear?.let { "Số $it" } ?: "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            Row {
                IconButton(onClick = onOpenApps) {
                    Icon(Icons.Default.Apps, contentDescription = "Apps")
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1.35f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(Modifier.fillMaxSize()) {
                    DolphinCarAnimation(
                        speedKmh = data.speedKmh ?: 0f,
                        gear = data.gear,
                        isCharging = data.isCharging,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            data.speedKmh?.let { "%.0f".format(it) } ?: "--",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text("km/h", color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Pin",
                    value = data.socPercent?.let { "%.0f".format(it) } ?: "--",
                    unit = "%",
                    icon = Icons.Default.BatteryChargingFull,
                    accent = batteryColor(data.socPercent),
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Quãng đường",
                    value = data.rangeKm?.let { "%.0f".format(it) } ?: "--",
                    unit = "km",
                    icon = Icons.Default.Route,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Công suất",
                    value = data.powerKw?.let { "%.1f".format(it) } ?: "--",
                    unit = "kW",
                    icon = Icons.Default.Bolt,
                    accent = if ((data.powerKw ?: 0f) < 0f) Color(0xFF66BB6A) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Áp suất lốp",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TpmsTile("Trước trái", data.tpmsFl, Modifier.weight(1f))
            TpmsTile("Trước phải", data.tpmsFr, Modifier.weight(1f))
            TpmsTile("Sau trái", data.tpmsRl, Modifier.weight(1f))
            TpmsTile("Sau phải", data.tpmsRr, Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TripChip("Hành trình", data.tripDistanceKm?.let { "%.1f km".format(it) } ?: "--")
                TripChip("Tiêu thụ", data.tripConsumptionKwhPer100km?.let { "%.1f".format(it) + " kWh/100" } ?: "--")
                TripChip("Thời gian", data.tripDurationMin?.let { "$it phút" } ?: "--")
                TripChip("Ngoài", data.outsideTempC?.let { "%.0f°C".format(it) } ?: "--")
            }
        }
    }
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    accent: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Text(unit, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun TpmsTile(label: String, kpa: Float?, modifier: Modifier = Modifier) {
    val color = when {
        kpa == null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
        kpa < 200f -> Color(0xFFE53935)
        kpa > 280f -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(
                kpa?.let { "%.0f".format(it) } ?: "--",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text("kPa", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun TripChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun batteryColor(soc: Float?): Color = when {
    soc == null -> Color.Gray
    soc < 15f -> Color(0xFFE53935)
    soc < 30f -> Color(0xFFFB8C00)
    else -> Color(0xFF43A047)
}
