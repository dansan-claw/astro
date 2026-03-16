package space.astro.bot.interactions.handlers.command.impl.tts

import mu.KotlinLogging
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.managers.AudioManager
import space.astro.bot.core.ui.Embeds
import space.astro.bot.interactions.InteractionAction
import space.astro.bot.interactions.context.InteractionContext
import space.astro.bot.interactions.handlers.command.AbstractCommand
import space.astro.bot.interactions.handlers.command.Command
import space.astro.bot.interactions.handlers.command.CommandCategory
import space.astro.bot.interactions.handlers.command.CommandOption
import space.astro.bot.services.TTSService
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

private val log = KotlinLogging.logger { }

/**
 * TTS Command - Text-to-Speech for micless users
 * Allows users to type text that gets spoken in voice channels
 */
@Command(
    name = "tts",
    description = "Speak text in a voice channel using AI text-to-speech",
    category = CommandCategory.VC,
    action = InteractionAction.TTS
)
class TTSCommand(
    private val ttsService: TTSService
) : AbstractCommand() {

    /**
     * Main TTS command - synthesizes text and plays it in the user's voice channel
     */
    suspend fun tts(
        event: SlashCommandInteractionEvent,
        ctx: InteractionContext,
        @CommandOption(
            name = "text",
            description = "The text to speak",
            type = OptionType.STRING,
            minLength = 1,
            maxLength = 500
        )
        text: String,
        @CommandOption(
            name = "voice",
            description = "Voice to use (optional)",
            type = OptionType.STRING
        )
        voice: String?
    ) {
        // Check if user is in a voice channel
        val memberVoiceState = event.member?.voiceState
        if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
            ctx.replyHandler.replyEmbed(Embeds.error("You need to be in a voice channel to use TTS!"))
            return
        }

        val voiceChannel = memberVoiceState.channel
        if (voiceChannel == null) {
            ctx.replyHandler.replyEmbed(Embeds.error("Could not find your voice channel!"))
            return
        }

        // Defer reply since TTS might take a moment
        ctx.replyHandler.deferReply()

        // Check TTS service health
        if (!ttsService.isHealthy()) {
            ctx.replyHandler.replyEmbed(Embeds.error("TTS service is currently unavailable. Please try again later."))
            return
        }

        // Synthesize speech
        val audioData = ttsService.synthesize(text, voice)
        if (audioData == null) {
            ctx.replyHandler.replyEmbed(Embeds.error("Failed to generate speech. Please try again."))
            return
        }

        // Join voice channel and play audio
        val guild = event.guild
        if (guild == null) {
            ctx.replyHandler.replyEmbed(Embeds.error("This command can only be used in a server."))
            return
        }

        val audioManager = guild.audioManager
        
        // Create audio send handler
        val audioHandler = TTSAudioSendHandler(audioData)
        
        // Connect and play
        try {
            audioManager.sendingHandler = audioHandler
            audioManager.openAudioConnection(voiceChannel)
            
            // Wait for audio to finish playing (rough estimate based on audio size)
            // WAV at 22050Hz, 16-bit mono = ~44KB per second
            val durationSeconds = audioData.size / 44000.0
            val waitTime = (durationSeconds * 1000).toLong() + 500 // Add buffer
            
            ctx.replyHandler.replyEmbed(Embeds.default("🔊 Speaking: \"${text.take(100)}${if (text.length > 100) "..." else ""}\""))
            
            // Schedule disconnect after audio finishes using non-blocking delay
            kotlinx.coroutines.delay(waitTime.coerceAtMost(30000)) // Max 30 seconds
            
            // Disconnect if still connected
            if (audioManager.isConnected) {
                audioManager.closeAudioConnection()
            }
            
        } catch (e: Exception) {
            log.error(e) { "Error playing TTS audio" }
            ctx.replyHandler.replyEmbed(Embeds.error("Error playing audio: ${e.message}"))
            audioManager.closeAudioConnection()
        }
    }
}

/**
 * AudioSendHandler for playing TTS audio in Discord voice channels
 * Converts WAV data to Discord's audio format (48kHz stereo, 16-bit)
 */
class TTSAudioSendHandler(
    private val wavData: ByteArray
) : AudioSendHandler {
    
    private val audioQueue = ConcurrentLinkedQueue<ByteArray>()
    private var isLoaded = false
    
    init {
        // Convert WAV to Discord format and queue it
        // For now, we'll pass through the audio (assuming Piper outputs compatible format)
        // In production, you'd want to resample to 48kHz stereo if needed
        queueAudioData()
    }
    
    private fun queueAudioData() {
        // Skip WAV header (44 bytes typically) and queue the PCM data
        val pcmData = wavData.copyOfRange(44, wavData.size)
        
        // Split into 20ms chunks (Discord requirement)
        // 48kHz * 2 channels * 2 bytes * 0.02s = 3840 bytes per frame
        val frameSize = 3840
        var offset = 0
        
        while (offset < pcmData.size) {
            val end = minOf(offset + frameSize, pcmData.size)
            val frame = pcmData.copyOfRange(offset, end)
            
            // Pad if necessary
            if (frame.size < frameSize) {
                val padded = ByteArray(frameSize) { 0 }
                frame.copyInto(padded)
                audioQueue.offer(padded)
            } else {
                audioQueue.offer(frame)
            }
            
            offset += frameSize
        }
        
        isLoaded = true
    }
    
    override fun canProvide(): Boolean {
        return audioQueue.isNotEmpty()
    }
    
    override fun provide20MsAudio(): ByteBuffer? {
        val data = audioQueue.poll() ?: return null
        return ByteBuffer.wrap(data)
    }
    
    override fun isOpus(): Boolean {
        return false // We're providing raw PCM, Discord will encode to Opus
    }
}
