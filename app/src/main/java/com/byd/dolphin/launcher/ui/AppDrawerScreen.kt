package com.byd.dolphin.launcher.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

data class AppItem(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: android.graphics.drawable.Drawable?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val apps = remember {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val list: List<ResolveInfo> = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        list.map {
            AppItem(
                label = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName,
                activityName = it.activityInfo.name,
                icon = it.loadIcon(pm)
            )
        }.sortedBy { it.label.lowercase() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ứng dụng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(apps) { app ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            val launch = Intent(Intent.ACTION_MAIN).apply {
                                setClassName(app.packageName, app.activityName)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            try {
                                context.startActivity(launch)
                            } catch (_: Exception) { }
                        }
                        .padding(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            app.icon?.let { d ->
                                val bmp = d.toBitmap(128, 128)
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = app.label,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        app.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(88.dp)
                    )
                }
            }
        }
    }
}
