package com.byd.dolphin.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.byd.dolphin.launcher.data.VehicleRepository
import com.byd.dolphin.launcher.ui.AppDrawerScreen
import com.byd.dolphin.launcher.ui.DashboardScreen
import com.byd.dolphin.launcher.ui.DolphinLauncherTheme
import com.byd.dolphin.launcher.ui.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.sin

sealed class Screen {
    data object Dashboard : Screen()
    data object Apps : Screen()
    data object Settings : Screen()
}

class MainActivity : ComponentActivity() {

    private lateinit var repo: VehicleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repo = VehicleRepository(applicationContext)

        setContent {
            var screen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
            var useDemo by remember { mutableStateOf(true) }
            val data by repo.data.collectAsState()

            LaunchedEffect(useDemo) {
                if (useDemo) {
                    var t = 0
                    while (isActive && useDemo) {
                        val speed = 40f + 25f * sin(t * 0.08f).toFloat()
                        repo.loadDemoData(animatedSpeed = speed)
                        t++
                        delay(400)
                    }
                } else {
                    while (isActive) {
                        repo.refresh()
                        delay(1500)
                    }
                }
            }

            DolphinLauncherTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (screen) {
                        Screen.Dashboard -> DashboardScreen(
                            data = data,
                            onOpenApps = { screen = Screen.Apps },
                            onOpenSettings = { screen = Screen.Settings }
                        )
                        Screen.Apps -> AppDrawerScreen(onBack = { screen = Screen.Dashboard })
                        Screen.Settings -> SettingsScreen(
                            onBack = { screen = Screen.Dashboard },
                            useDemoData = useDemo,
                            onToggleDemo = { useDemo = it }
                        )
                    }
                }
            }
        }
    }
}
