package com.example.voiceaccess.command

import java.util.Locale

/**
 * Converts a small, predictable set of spoken phrases into typed commands.
 * A future AI/Python parser can return the same VoiceCommand values.
 */
class CommandProcessor {
    fun process(spokenText: String): VoiceCommand {
        val text = spokenText
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        return when {
            text.matches(Regex("(open|launch|start) (the )?whats ?app")) -> VoiceCommand.OpenWhatsApp
            text.matches(Regex("(open|launch|start) (the )?you ?tube")) -> VoiceCommand.OpenYouTube
            text.matches(Regex("(open|launch|start) (the )?camera")) -> VoiceCommand.OpenCamera
            text in setOf("go home", "home", "go to home") -> VoiceCommand.GoHome
            text in setOf("go back", "back") -> VoiceCommand.GoBack
            text in setOf("scroll up", "move up", "swipe up") -> VoiceCommand.ScrollUp
            text in setOf("scroll down", "move down", "swipe down") -> VoiceCommand.ScrollDown
            else -> VoiceCommand.Unknown(spokenText)
        }
    }
}
