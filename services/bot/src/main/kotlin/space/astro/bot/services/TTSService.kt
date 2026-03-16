package space.astro.bot.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream

private val log = KotlinLogging.logger { }

/**
 * Service for Text-to-Speech using local Piper TTS
 */
@Service
class TTSService(
    @Value("\${piper.tts.url:http://localhost:8100}") private val ttsBaseUrl: String
) {
    private val httpClient = HttpClient(Apache) {
        install(ContentNegotiation) {
            jackson()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 5000
        }
    }

    /**
     * Generate speech from text using Piper TTS
     * @param text The text to synthesize
     * @param voice Optional voice to use (defaults to service default)
     * @return ByteArray of WAV audio data, or null if failed
     */
    suspend fun synthesize(text: String, voice: String? = null): ByteArray? {
        return try {
            log.debug { "Synthesizing TTS for text: ${text.take(50)}..." }
            
            val response = httpClient.get("$ttsBaseUrl/tts") {
                parameter("text", text)
                voice?.let { parameter("voice", it) }
            }

            if (response.status.isSuccess()) {
                response.body<ByteArray>().also {
                    log.debug { "TTS synthesis successful, received ${it.size} bytes" }
                }
            } else {
                log.error { "TTS synthesis failed: ${response.status}" }
                null
            }
        } catch (e: Exception) {
            log.error(e) { "Failed to synthesize TTS" }
            null
        }
    }

    /**
     * Check if TTS service is healthy
     */
    suspend fun isHealthy(): Boolean {
        return try {
            val response = httpClient.get("$ttsBaseUrl/health")
            response.status.isSuccess()
        } catch (e: Exception) {
            log.warn { "TTS service health check failed: ${e.message}" }
            false
        }
    }

    /**
     * Get available voices from TTS service
     */
    suspend fun getAvailableVoices(): List<String> {
        return try {
            val response = httpClient.get("$ttsBaseUrl/voices")
            if (response.status.isSuccess()) {
                val voicesResponse: VoicesResponse = response.body()
                voicesResponse.voices
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            log.warn { "Failed to get voices: ${e.message}" }
            emptyList()
        }
    }
}

/**
 * Response from /voices endpoint
 */
data class VoicesResponse(
    val voices: List<String>,
    val default: String
)
