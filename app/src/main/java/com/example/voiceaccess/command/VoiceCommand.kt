package com.example.voiceaccess.command

/** Commands that the Kotlin prototype knows how to perform. */
sealed interface VoiceCommand {
    data object OpenWhatsApp : VoiceCommand
    data object OpenYouTube : VoiceCommand
    data object OpenCamera : VoiceCommand
    data object GoHome : VoiceCommand
    data object GoBack : VoiceCommand
    data object ScrollUp : VoiceCommand
    data object ScrollDown : VoiceCommand
    data class Unknown(val spokenText: String) : VoiceCommand
}
