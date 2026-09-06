package com.example.voiceaccess.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Android creates this service only after the person explicitly enables it in Settings.
 * It records the active package for display and exposes safe actions to CommandExecutor.
 */
class VoiceAccessibilityService : AccessibilityService() {
    lateinit var actions: AccessibilityActions
        private set

    var activePackageName: String? = null
        private set

    override fun onServiceConnected() {
        super.onServiceConnected()
        actions = AccessibilityActions(this)
        currentService = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString()
        if (!packageName.isNullOrBlank()) activePackageName = packageName
        // Future versions can inspect the accessible node tree here only for requested actions.
    }

    override fun onInterrupt() {
        // No ongoing spoken feedback is produced by this prototype.
    }

    override fun onDestroy() {
        if (currentService === this) currentService = null
        super.onDestroy()
    }

    companion object {
        @Volatile
        var currentService: VoiceAccessibilityService? = null
            private set
    }
}
