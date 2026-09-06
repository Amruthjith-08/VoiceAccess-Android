package com.example.voiceaccess.command

import android.content.Context
import com.example.voiceaccess.accessibility.VoiceAccessibilityService

/** Executes commands only after they have been recognized and parsed. */
class CommandExecutor(context: Context) {
    private val appLauncher = AppLauncher(context.applicationContext)

    fun execute(command: VoiceCommand): String = when (command) {
        VoiceCommand.OpenWhatsApp -> appLauncher.launchWhatsApp()
        VoiceCommand.OpenYouTube -> appLauncher.launchYouTube()
        VoiceCommand.OpenCamera -> appLauncher.launchCamera()
        VoiceCommand.GoBack -> requireService("go back") { it.actions.performBack() }
        VoiceCommand.GoHome -> requireService("go home") { it.actions.performHome() }
        VoiceCommand.ScrollUp -> requireService("scroll up") { it.actions.scrollUp() }
        VoiceCommand.ScrollDown -> requireService("scroll down") { it.actions.scrollDown() }
        is VoiceCommand.Unknown -> "I do not know the command: \"${command.spokenText}\"."
    }

    private fun requireService(actionName: String, block: (VoiceAccessibilityService) -> Boolean): String {
        val service = VoiceAccessibilityService.currentService
            ?: return "To $actionName, enable VoiceAccess in Accessibility Settings first."
        return if (block(service)) "$actionName completed." else "I could not $actionName on this screen."
    }
}
