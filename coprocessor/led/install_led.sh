#!/bin/bash

# Exit on any error
set -e

echo "--- 🛠️ Starting LED Service Installation ---"

# 1. System Dependencies
sudo apt-get update
sudo apt-get install -y python3-pip curl

# 2. Install UV (if missing)
if ! command -v uv &> /dev/null; then
    echo "Installing uv..."
    curl -LsSf https://astral.sh/uv/install.sh | sh
    source $HOME/.cargo/env
fi

# 3. Configure Hardware (Disable Audio for PWM)
# Required for NeoPixel control on GPIO 18
CONFIG_PATH="/boot/firmware/config.txt"
[ ! -f "$CONFIG_PATH" ] && CONFIG_PATH="/boot/config.txt"

if grep -q "dtparam=audio=on" "$CONFIG_PATH"; then
    echo "Disabling onboard audio to free up PWM for LEDs..."
    sudo sed -i 's/dtparam=audio=on/dtparam=audio=off/' "$CONFIG_PATH"
    echo "⚠️ Hardware change detected. YOU MUST REBOOT after this script finishes."
fi

# 4. Project Setup
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

echo "Syncing dependencies with uv..."
# Creates .venv and installs everything in pyproject.toml
uv sync

# 5. Copy and Enable Systemd Service
SERVICE_FILE="led.service"

if [ -f "$SERVICE_FILE" ]; then
    echo "Installing $SERVICE_FILE..."
    sudo cp "$SERVICE_FILE" /etc/systemd/system/
    sudo systemctl daemon-reload
    sudo systemctl enable led.service
    sudo systemctl restart led.service
    echo "Service 'led.service' installed and started."
else
    echo "❌ Error: $SERVICE_FILE not found in the current directory."
    exit 1
fi

echo "-----------------------------------------------"
echo "✅ Installation Complete."
echo "Current Status: $(sudo systemctl is-active led.service)"
echo "-----------------------------------------------"