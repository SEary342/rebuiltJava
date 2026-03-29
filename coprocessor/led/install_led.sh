#!/bin/bash

echo "--- Installing LED Service Dependencies ---"
sudo apt-get update
sudo apt-get install -y python3-pip curl

# Install UV for fast env management (similar to your gyro setup)
if ! command -v uv &> /dev/null; then
    curl -LsSf https://astral.sh/uv/install.sh | sh
    export PATH="$HOME/.local/bin:$PATH"
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

# Setup environment
uv venv
source .venv/bin/python
uv pip install adafruit-circuitpython-neopixel robotpy-ntcore

# Setup Systemd Service
SERVICE_FILE="led.service"
if [ -f "$SERVICE_FILE" ]; then
    # Update paths in the service file
    sed -i "s|WorkingDirectory=.*|WorkingDirectory=$DIR|" "$SERVICE_FILE"
    sed -i "s|ExecStart=.*|ExecStart=$DIR/.venv/bin/python led_service.py|" "$SERVICE_FILE"
    
    sudo cp "$SERVICE_FILE" /etc/systemd/system/
    sudo systemctl daemon-reload
    sudo systemctl enable led.service
    sudo systemctl start led.service
    echo "Service 'led.service' installed and started."
else
    echo "Error: led.service not found."
fi

echo "Installation Complete."
echo "To test from the robot, set the 'LEDs/state' NetworkTable string."