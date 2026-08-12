package com.byd.dolphin.launcher.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    useDemoData: Boolean,
    onToggleDemo: (Boolean) -> Unit
) {
    var nightMode by remember {
        mutableIntStateOf(AppCompatDelegate.getDefaultNightMode())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Giao diện", style = MaterialTheme.typography.titleMedium)

            SettingSwitch(
                title = "Tự động sáng / tối",
                subtitle = "Theo chế độ hệ thống DiLink",
                checked = nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                onCheckedChange = { checked ->
                    val mode = if (checked) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else AppCompatDelegate.MODE_NIGHT_NO
                    nightMode = mode
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            )

            SettingSwitch(
                title = "Luôn tối",
                subtitle = "Buộc dark theme",
                checked = nightMode == AppCompatDelegate.MODE_NIGHT_YES,
                onCheckedChange = { checked ->
                    val mode = if (checked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    nightMode = mode
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            )

            HorizontalDivider()

            Text("Dữ liệu xe", style = MaterialTheme.typography.titleMedium)

            SettingSwitch(
                title = "Dữ liệu demo",
                subtitle = "Bật khi phát triển / chưa map API thật trên xe",
                checked = useDemoData,
                onCheckedChange = onToggleDemo
            )

            HorizontalDivider()

            Text("Hướng dẫn", style = MaterialTheme.typography.titleMedium)
            Text(
                "1. Build APK bằng Android Studio (JDK 17).\n" +
                        "2. Cài lên xe qua USB (Third Party Apps XX) hoặc ADB.\n" +
                        "3. Set làm Home: Cài đặt → Ứng dụng mặc định → Màn hình chính.\n" +
                        "4. Trên xe: adb shell content query --uri content://com.byd.carStatusProvider/car_status\n" +
                        "5. Map key thật vào VehicleRepository.mapProviderKeys().\n" +
                        "6. Tham khảo byd-dolphin-hacking & byd-apps để gọi BYDAuto API / TPMS chính xác.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Phiên bản 0.2.0 — open source MIT\nResearch: wheregoes/byd-dolphin-hacking",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
