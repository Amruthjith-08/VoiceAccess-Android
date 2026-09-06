package com.example.voiceaccess.command

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore

/** Launches only installed applications by asking Android's PackageManager first. */
class AppLauncher(private val context: Context) {
    fun launchWhatsApp(): String = launchPackage("com.whatsapp", "WhatsApp")

    fun launchYouTube(): String = launchPackage("com.google.android.youtube", "YouTube")

    fun launchCamera(): String {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val resolved = intent.resolveActivity(context.packageManager)
            ?: return "No camera application is available on this phone."
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Opening camera."
    }

    private fun launchPackage(packageName: String, appName: String): String {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return "$appName is not installed or cannot be launched."
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            "Opening $appName."
        } catch (exception: SecurityException) {
            "$appName could not be opened: ${exception.message ?: "permission denied"}."
        }
    }
}
