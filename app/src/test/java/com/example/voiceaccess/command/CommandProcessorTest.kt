package com.example.voiceaccess.command

import org.junit.Assert.assertEquals
import org.junit.Test

class CommandProcessorTest {
    private val processor = CommandProcessor()

    @Test fun `normalizes app commands`() {
        assertEquals(VoiceCommand.OpenWhatsApp, processor.process("Launch Whats App!"))
        assertEquals(VoiceCommand.OpenYouTube, processor.process("OPEN YOUTUBE"))
    }

    @Test fun `recognizes navigation commands`() {
        assertEquals(VoiceCommand.GoHome, processor.process("go to home"))
        assertEquals(VoiceCommand.ScrollDown, processor.process("swipe down"))
    }
}
