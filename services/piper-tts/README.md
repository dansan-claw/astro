# Piper TTS Service

Local, free text-to-speech service for the Astro Discord bot.

## Overview

This service provides a simple HTTP API around [Piper](https://github.com/rhasspy/piper) - a fast, local neural text-to-speech system.

## Features

- ✅ **100% Free** - Runs locally, no API costs
- ✅ **Fast** - Real-time synthesis on CPU
- ✅ **Good Quality** - Neural voices
- ✅ **Simple HTTP API** - Easy integration

## Quick Start

### Using Docker Compose (Recommended)

The service is included in the main `docker/docker-compose-dev.yml`:

```bash
cd docker
docker compose -f docker-compose-dev.yml up -d piper-tts
```

### Manual Build

```bash
cd services/piper-tts
docker build -t piper-tts .
docker run -p 8100:8000 piper-tts
```

## API Endpoints

### Health Check
```bash
curl http://localhost:8100/health
```

### List Voices
```bash
curl http://localhost:8100/voices
```

### Text to Speech (GET)
```bash
curl "http://localhost:8100/tts?text=Hello%20world" -o output.wav
```

### Text to Speech (POST)
```bash
curl -X POST http://localhost:8100/tts \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello world", "voice": "en_US-lessac-medium"}' \
  -o output.wav
```

## Configuration

| Environment Variable | Default | Description |
|---------------------|---------|-------------|
| `PIPER_BINARY` | `/usr/local/bin/piper` | Path to piper executable |
| `VOICES_DIR` | `/data/piper-voices` | Directory containing voice models |
| `DEFAULT_VOICE` | `en_US-lessac-medium` | Default voice to use |

## Available Voices

Voices are downloaded from [Hugging Face](https://huggingface.co/rhasspy/piper-voices).

Default included:
- `en_US-lessac-medium` - English (US), medium quality

To add more voices, download and extract to the voices directory.

## Integration with Astro Bot

The bot service can call this TTS service via HTTP:

```kotlin
// Example: TTS client in Kotlin
val ttsClient = HttpClient()
suspend fun speak(text: String, voice: String = "en_US-lessac-medium"): ByteArray {
    val response = ttsClient.get("http://piper-tts:8000/tts") {
        parameter("text", text)
        parameter("voice", voice)
    }
    return response.body()
}
```

## Resource Usage

- **RAM**: ~100MB per voice loaded
- **CPU**: Low (real-time on modern CPU)
- **Storage**: ~50-100MB per voice model

## License

Same as Astro bot (AGPL-3.0)
