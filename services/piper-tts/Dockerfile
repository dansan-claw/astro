FROM python:3.11-slim

# Install dependencies
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    libsndfile1 \
    && rm -rf /var/lib/apt/lists/*

# Install Piper
RUN wget -O /tmp/piper.tar.gz \
    https://github.com/rhasspy/piper/releases/download/v1.2.0/piper_amd64.tar.gz \
    && tar -xzf /tmp/piper.tar.gz -C /usr/local \
    && rm /tmp/piper.tar.gz \
    && ln -s /usr/local/piper/piper /usr/local/bin/piper

# Create voices directory
RUN mkdir -p /data/piper-voices

# Download default voice (en_US-lessac-medium)
RUN wget -O /tmp/voice.tar.gz \
    https://huggingface.co/rhasspy/piper-voices/resolve/v1.0.0/en/en_US/lessac/medium/en_US-lessac-medium.tar.gz \
    && tar -xzf /tmp/voice.tar.gz -C /data/piper-voices \
    && rm /tmp/voice.tar.gz

# Set up Python service
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ ./src/

# Environment
ENV PIPER_BINARY=/usr/local/bin/piper
ENV VOICES_DIR=/data/piper-voices
ENV DEFAULT_VOICE=en_US-lessac-medium
ENV PYTHONUNBUFFERED=1

EXPOSE 8000

CMD ["python", "-m", "uvicorn", "src.main:app", "--host", "0.0.0.0", "--port", "8000"]
