# Astro AI Customization Plan

## Fork Status
✅ Forked from `bot-astro/astro` to `dansan-claw/astro`

## Architecture Overview
- **Language:** Kotlin (Spring Boot)
- **Services:** 4 microservices (bot, central-api, entitlements-job, support-bot)
- **Dependencies:** MongoDB, Redis, Discord JDA
- **Build:** Gradle with Kotlin DSL

## Planned AI Features

### 1. 🎙️ AI Voice Transcription (STT)
**Purpose:** Real-time transcription of voice channel audio using OpenAI Whisper API

**Implementation Plan:**
- Create new service: `services/transcription`
- Hook into Discord voice receive events via JDA
- Stream audio chunks to Whisper API
- Post transcriptions to text channel or overlay

**Key Components:**
- `AudioReceiveHandler` - Capture voice audio from Discord
- `TranscriptionService` - Send audio to Whisper API
- `TranscriptionPublisher` - Send results back to Discord

### 2. 🔊 Text-to-Speech (TTS) for Micless Users
**Purpose:** Allow users without microphones to speak via text commands

**Implementation Plan:**
- Add new slash command: `/tts "message"`
- Integrate OpenAI TTS API (or ElevenLabs)
- Bot joins voice channel and plays synthesized audio
- Optional: Auto-join when TTS is requested

**Key Components:**
- `TTSCommand` - Slash command handler
- `TTSService` - Generate audio from text
- `VoiceAudioPlayer` - Play audio in Discord voice channel

### 3. 📝 AI Meeting Summaries
**Purpose:** Generate summaries of voice channel conversations

**Implementation Plan:**
- Store transcriptions in Redis/MongoDB
- Trigger summary generation on command or auto-interval
- Use GPT-4 to summarize conversation threads
- Post summary to designated channel

## Technical Considerations

### Audio Handling
- Discord voice audio: 48kHz stereo Opus
- Whisper requires: 16kHz mono WAV/MP3
- Need audio format conversion (FFmpeg)

### Rate Limits & Costs
- Whisper API: $0.006/minute
- TTS API: $0.015/1K characters
- Implement user quotas and cooldowns

### Privacy
- Store audio temporarily (transient)
- Opt-in per guild/user
- Clear data retention policies

## Implementation Phases

### Phase 1: TTS (Easier)
- [ ] Add TTS slash command to bot service
- [ ] Integrate OpenAI TTS API
- [ ] Voice channel audio playback
- [ ] Configuration in env files

### Phase 2: Transcription (Harder)
- [ ] Create transcription service
- [ ] Discord voice receive handler
- [ ] Audio format conversion
- [ ] Whisper API integration
- [ ] Real-time transcription display

### Phase 3: Summaries (Bonus)
- [ ] Transcription storage
- [ ] GPT-4 summarization
- [ ] Summary commands and scheduling

## Next Steps
1. Set up local development environment
2. Create feature branches
3. Implement TTS first (lower complexity)
4. Test with Discord bot token
5. Iterate and refine

---
*Created: 2026-03-15*
