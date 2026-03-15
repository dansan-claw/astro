# Astro AI Customization Plan

## Fork Status
✅ Forked from `bot-astro/astro` to `dansan-claw/astro`

## Architecture Overview
- **Language:** Kotlin (Spring Boot)
- **Services:** 4 microservices (bot, central-api, entitlements-job, support-bot)
- **Dependencies:** MongoDB, Redis, Discord JDA
- **Build:** Gradle with Kotlin DSL

## Planned AI Features

### 1. 🔊 Text-to-Speech (TTS) for Micless Users - FREE/LOCAL
**Purpose:** Allow users without microphones to speak via text commands using local AI

**Implementation Plan:**
- Add new slash command: `/tts "message" [voice]`
- Run **Piper** (fast local neural TTS) as a sidecar service
- Bot joins voice channel and plays synthesized audio
- Optional: Auto-join when TTS is requested

**Why Piper?**
- ✅ **100% free** - runs locally on your server
- ✅ **Fast** - real-time synthesis on CPU
- ✅ **Good quality** - neural voices
- ✅ **Small models** - ~50-100MB per voice
- ✅ **Easy HTTP API** - can wrap in a simple service

**Alternative Options Considered:**
| Option | Cost | Quality | Speed | Notes |
|--------|------|---------|-------|-------|
| **Piper** ⭐ | Free | Good | Fast | Best balance, local |
| Coqui TTS | Free | Great | Medium | Heavier, more setup |
| eSpeak-NG | Free | Robotic | Fast | Very lightweight |
| OpenAI TTS | $0.015/1K chars | Excellent | Fast | Cloud, costs $ |
| ElevenLabs | $5/mo+ | Excellent | Fast | Cloud, freemium |

**Key Components:**
- `TTSCommand` - Slash command handler
- `PiperClient` - HTTP client to Piper service
- `VoiceAudioPlayer` - Play audio in Discord voice channel

### 2. 🎙️ AI Voice Transcription (STT) - FREE/LOCAL
**Purpose:** Real-time transcription of voice channel audio

**Implementation Plan:**
- Create new service: `services/transcription`
- Use **Whisper.cpp** (local Whisper implementation) or **faster-whisper**
- Hook into Discord voice receive events via JDA
- Post transcriptions to text channel

**Why Local Whisper?**
- ✅ **Free** - runs locally
- ✅ **Privacy** - audio never leaves your server
- ✅ **Good accuracy** - OpenAI Whisper quality
- ✅ **Multiple sizes** - tiny (39MB) to large (3GB)

**Key Components:**
- `AudioReceiveHandler` - Capture voice audio from Discord
- `WhisperClient` - Send audio to local Whisper service
- `TranscriptionPublisher` - Send results back to Discord

### 3. 📝 AI Meeting Summaries (Optional)
**Purpose:** Generate summaries of voice channel conversations

**Implementation Plan:**
- Store transcriptions in Redis/MongoDB
- Use local LLM (llama.cpp, ollama) or free tier APIs
- Trigger summary generation on command
- Post summary to designated channel

## Technical Architecture

### New Services to Add
```
services/
├── bot/                    # Existing Discord bot
├── central-api/            # Existing REST API
├── piper-tts/              # NEW: Piper TTS HTTP wrapper
│   ├── Dockerfile
│   └── src/
│       └── main.py         # FastAPI wrapper around Piper
├── whisper-stt/            # NEW: Whisper transcription service
│   ├── Dockerfile
│   └── src/
│       └── main.py         # FastAPI + faster-whisper
└── ...
```

### Docker Compose Setup
```yaml
services:
  piper-tts:
    image: rhasspy/wyoming-piper
    volumes:
      - piper-voices:/data
    ports:
      - "10200:10200"
  
  whisper-stt:
    image: fedirz/faster-whisper-server
    environment:
      - WHISPER_MODEL=base
    ports:
      - "8000:8000"
```

## Implementation Phases

### Phase 1: TTS with Piper (Easiest)
- [ ] Create `services/piper-tts` with Docker container
- [ ] Add TTS slash command to bot service
- [ ] HTTP client to communicate with Piper
- [ ] Voice channel audio playback via JDA
- [ ] Configuration in env files

### Phase 2: Transcription with Whisper (Harder)
- [ ] Create `services/whisper-stt` with Docker container
- [ ] Discord voice receive handler
- [ ] Audio format conversion (Opus → WAV)
- [ ] Whisper API integration
- [ ] Real-time transcription display

### Phase 3: Summaries (Bonus)
- [ ] Transcription storage
- [ ] Local LLM integration (ollama)
- [ ] Summary commands and scheduling

## Resource Requirements

### Piper TTS
- **RAM:** ~100MB per voice loaded
- **CPU:** Low (real-time on modern CPU)
- **Storage:** ~50-100MB per voice model
- **GPU:** Optional (CPU is fine)

### Whisper STT
- **RAM:** ~500MB (base model) to 6GB (large model)
- **CPU:** Moderate (base model is fast)
- **Storage:** 39MB (tiny) to 3GB (large)
- **GPU:** Optional, speeds things up

## Next Steps
1. ✅ Fork Astro (done)
2. Set up local development environment
3. Create `services/piper-tts` Docker service
4. Add `/tts` command to bot
5. Test with Discord bot token
6. Iterate and refine

---
*Created: 2026-03-15*
*Updated: 2026-03-15 - Switched to free/local AI options*
