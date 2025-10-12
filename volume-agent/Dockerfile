FROM python:3.10-slim

# Install OS dependiences
RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Add new user
RUN useradd -ms /bin/bash agentuser
RUN  usermod -aG video agentuser

# Switch user b/c root is not longer needed
USER agentuser

# Ensure YOLO uses the user .config directory instead of /tmp
RUN mkdir -p /home/agentuser/.config/Ultralytics \
    && chown -R agentuser:agentuser /home/agentuser/.config

ENV YOLO_CONFIG_DIR=/home/agentuser/.config/Ultralytics
ENV PATH=$PATH:/home/agentuser/.local/bin

WORKDIR /home/agentuser/

# Copy project files
COPY --chown=agentuser:agentuser ./ ./agent/

WORKDIR /home/agentuser/agent

# Install python dependiences
RUN pip install --no-cache-dir --upgrade pip \
    && pip install --no-cache-dir -r requirements.txt

CMD ["python", "main.py"]


