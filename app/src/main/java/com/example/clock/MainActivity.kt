package com.example.clock

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var isFloating by mutableStateOf(false)

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            startFloatingService()
        } else {
            Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setGravity(Gravity.TOP or Gravity.START)
        window.setLayout(
            (300 * resources.displayMetrics.density).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setContent {
            FloatingMainWindow(
                isFloating = isFloating,
                onStartFloating = { checkAndStartFloating() },
                onStopFloating = { stopFloatingService() },
                onClose = { finish() },
                onDrag = { dx, dy -> moveWindow(dx, dy) }
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startFloatingService()
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                overlayPermissionLauncher.launch(intent)
            }
        } else {
            startFloatingService()
        }
    }

    private fun moveWindow(dx: Float, dy: Float) {
        val params = window.attributes
        params.x = (params.x + dx).toInt()
        params.y = (params.y + dy).toInt()
        window.attributes = params
    }

    private fun checkAndStartFloating() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startFloatingService()
            } else {
                Toast.makeText(this, "Please grant overlay permission", Toast.LENGTH_LONG).show()
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                overlayPermissionLauncher.launch(intent)
            }
        } else {
            startFloatingService()
        }
    }

    private fun startFloatingService() {
        try {
            val intent = Intent(this, FloatingWindowService::class.java)
            startService(intent)
            isFloating = true
            Toast.makeText(this, "Floating clock started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to start service", e)
            Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopFloatingService() {
        val intent = Intent(this, FloatingWindowService::class.java)
        stopService(intent)
        isFloating = false
        Toast.makeText(this, "Floating clock stopped", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun FloatingMainWindow(
    isFloating: Boolean,
    onStartFloating: () -> Unit,
    onStopFloating: () -> Unit,
    onClose: () -> Unit,
    onDrag: (Float, Float) -> Unit
) {
    var currentTime by remember { mutableStateOf(getCurrentTimeString()) }
    var currentDate by remember { mutableStateOf(getCurrentDateString()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTimeString()
            currentDate = getCurrentDateString()
            delay(1000L)
        }
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEE1A1A2E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            var startX = 0f
                            var startY = 0f
                            while (true) {
                                val event = awaitPointerEvent()
                                when (event.type) {
                                    androidx.compose.ui.input.pointer.PointerEventType.Press -> {
                                        startX = event.changes.first().position.x
                                        startY = event.changes.first().position.y
                                    }
                                    androidx.compose.ui.input.pointer.PointerEventType.Move -> {
                                        val change = event.changes.first()
                                        val dx = change.position.x - startX
                                        val dy = change.position.y - startY
                                        if (kotlin.math.abs(dx) > 5 || kotlin.math.abs(dy) > 5) {
                                            onDrag(dx, dy)
                                            startX = change.position.x
                                            startY = change.position.y
                                        }
                                    }
                                }
                            }
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clock",
                    color = Color(0xFF888888),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(
                        text = "X",
                        color = Color(0xFF888888),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentDate,
                color = Color(0xFFAAAAAA),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentTime,
                color = Color(0xFF4CAF50),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = if (isFloating) onStopFloating else onStartFloating,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFloating) Color(0xFFE53935) else Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = if (isFloating) "Stop" else "Start",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Developer: xts",
                color = Color(0xFF555555),
                fontSize = 10.sp
            )
        }
    }
}

private fun getCurrentTimeString(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}

private fun getCurrentDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}
