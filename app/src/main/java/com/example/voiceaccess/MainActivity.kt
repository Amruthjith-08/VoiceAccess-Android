package com.example.voiceaccess

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.voiceaccess.accessibility.VoiceAccessibilityService
import com.example.voiceaccess.command.CommandExecutor
import com.example.voiceaccess.command.CommandProcessor
import com.example.voiceaccess.speech.VoiceRecognizer

class MainActivity : ComponentActivity() {
    private lateinit var voiceRecognizer: VoiceRecognizer
    private val commandProcessor = CommandProcessor()
    private lateinit var commandExecutor: CommandExecutor

    private var serviceEnabled by mutableStateOf(false)
    private var recognizedText by mutableStateOf("Press the microphone and say a command.")
    private var statusText by mutableStateOf("Ready")

    private val microphonePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) voiceRecognizer.startListening()
        else statusText = "Microphone permission is needed for voice input."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commandExecutor = CommandExecutor(applicationContext)
        voiceRecognizer = VoiceRecognizer(
            context = applicationContext,
            onResult = { spokenText ->
                recognizedText = spokenText
                statusText = commandExecutor.execute(commandProcessor.process(spokenText))
            },
            onStatus = { statusText = it },
        )

        setContent {
            VoiceAccessScreen(
                serviceEnabled = serviceEnabled,
                recognizedText = recognizedText,
                statusText = statusText,
                onMicrophoneClick = ::startVoiceInput,
                onAccessibilitySettingsClick = {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                },
                onSettingsClick = ::openAppSettings,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        serviceEnabled = isVoiceAccessibilityServiceEnabled(this)
    }

    override fun onDestroy() {
        voiceRecognizer.destroy()
        super.onDestroy()
    }

    private fun startVoiceInput() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            voiceRecognizer.startListening()
        } else {
            microphonePermission.launch(permission)
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

private fun isVoiceAccessibilityServiceEnabled(context: Context): Boolean {
    val manager = context.getSystemService(AccessibilityManager::class.java) ?: return false
    return manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK).any { info ->
        info.resolveInfo.serviceInfo.run {
            packageName == context.packageName && name == VoiceAccessibilityService::class.java.name
        }
    }
}

@androidx.compose.runtime.Composable
private fun VoiceAccessScreen(
    serviceEnabled: Boolean,
    recognizedText: String,
    statusText: String,
    onMicrophoneClick: () -> Unit,
    onAccessibilitySettingsClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val accent = if (serviceEnabled) Color(0xFF00E69B) else Color(0xFFFFB4AB)
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Voice Access", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))
                Text(
                    text = if (serviceEnabled) "VOICE CONTROL ON" else "VOICE CONTROL OFF",
                    color = accent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(28.dp))
                Button(
                    onClick = onMicrophoneClick,
                    modifier = Modifier.fillMaxWidth().height(104.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C4C)),
                ) {
                    Text("🎤  START LISTENING", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(24.dp))
                Text("Heard", color = Color.White, fontSize = 18.sp)
                Text(
                    text = recognizedText,
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF202020)).padding(16.dp),
                    color = Color.White,
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(statusText, color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(28.dp))
                Button(onClick = onAccessibilitySettingsClick, modifier = Modifier.fillMaxWidth().height(64.dp)) {
                    Text("OPEN ACCESSIBILITY SETTINGS", fontSize = 18.sp)
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = onSettingsClick, modifier = Modifier.fillMaxWidth().height(58.dp)) {
                    Text("APP SETTINGS", fontSize = 18.sp)
                }
            }
        }
    }
}
