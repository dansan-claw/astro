#!/usr/bin/env python3
"""
Piper TTS HTTP Service
A simple FastAPI wrapper around Piper for local text-to-speech.
"""

import os
import io
import subprocess
import tempfile
from pathlib import Path
from typing import Optional

from fastapi import FastAPI, HTTPException, Query
from fastapi.responses import StreamingResponse
from pydantic import BaseModel

app = FastAPI(title="Piper TTS Service", version="1.0.0")

# Configuration
PIPER_BINARY = os.getenv("PIPER_BINARY", "/usr/local/bin/piper")
VOICES_DIR = Path(os.getenv("VOICES_DIR", "/data/piper-voices"))
DEFAULT_VOICE = os.getenv("DEFAULT_VOICE", "en_US-lessac-medium")


class TTSRequest(BaseModel):
    text: str
    voice: Optional[str] = None
    speed: Optional[float] = 1.0


class TTSResponse(BaseModel):
    success: bool
    voice_used: str
    text_length: int


@app.get("/")
async def root():
    return {"service": "Piper TTS", "status": "running"}


@app.get("/voices")
async def list_voices():
    """List available voices."""
    voices = []
    if VOICES_DIR.exists():
        for voice_dir in VOICES_DIR.iterdir():
            if voice_dir.is_dir():
                voices.append(voice_dir.name)
    return {"voices": voices, "default": DEFAULT_VOICE}


@app.post("/tts")
async def text_to_speech(request: TTSRequest):
    """
    Convert text to speech using Piper.
    Returns WAV audio stream.
    """
    voice = request.voice or DEFAULT_VOICE
    text = request.text.strip()
    
    if not text:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    
    # Find voice model
    voice_path = VOICES_DIR / voice
    model_file = voice_path / f"{voice}.onnx"
    config_file = voice_path / f"{voice}.onnx.json"
    
    if not model_file.exists():
        available = await list_voices()
        raise HTTPException(
            status_code=404, 
            detail=f"Voice '{voice}' not found. Available: {available['voices']}"
        )
    
    try:
        # Create temporary output file
        with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp_file:
            output_path = tmp_file.name
        
        # Build Piper command
        cmd = [
            PIPER_BINARY,
            "--model", str(model_file),
            "--output_file", output_path,
            "--sentence-silence", "0.2",
        ]
        
        # Add config if exists
        if config_file.exists():
            cmd.extend(["--config", str(config_file)])
        
        # Run Piper
        process = subprocess.run(
            cmd,
            input=text.encode(),
            capture_output=True,
            timeout=30
        )
        
        if process.returncode != 0:
            raise HTTPException(
                status_code=500, 
                detail=f"Piper failed: {process.stderr.decode()}"
            )
        
        # Read output file
        with open(output_path, "rb") as f:
            audio_data = f.read()
        
        # Cleanup
        os.unlink(output_path)
        
        # Return audio stream
        return StreamingResponse(
            io.BytesIO(audio_data),
            media_type="audio/wav",
            headers={
                "X-Voice-Used": voice,
                "X-Text-Length": str(len(text))
            }
        )
        
    except subprocess.TimeoutExpired:
        raise HTTPException(status_code=504, detail="TTS generation timed out")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/tts")
async def text_to_speech_get(
    text: str = Query(..., description="Text to synthesize"),
    voice: Optional[str] = Query(None, description="Voice to use"),
    speed: Optional[float] = Query(1.0, description="Speech speed multiplier")
):
    """GET endpoint for TTS (easier for testing)."""
    return await text_to_speech(TTSRequest(text=text, voice=voice, speed=speed))


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {
        "status": "healthy",
        "piper_binary": Path(PIPER_BINARY).exists(),
        "voices_dir": str(VOICES_DIR),
        "voices_dir_exists": VOICES_DIR.exists()
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
