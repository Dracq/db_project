#!/bin/bash
set -e

echo "========================================"
echo " Setting up ReconX Environment"
echo "========================================"

# Update and install basic tools
echo "[1/6] Installing system dependencies (git, curl, postgresql-client, unzip, zip, jq)..."
if command -v apt-get &> /dev/null; then
    sudo apt-get update
    sudo apt-get install -y curl wget git unzip zip postgresql-client jq
else
    echo "Warning: apt-get not found. Please install git, curl, postgresql-client, unzip, zip manually."
fi

# Install Docker
if ! command -v docker &> /dev/null; then
    echo "[2/6] Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    echo "Docker installed. You may need to log out and log back in for group changes to take effect."
    rm get-docker.sh
else
    echo "[2/6] Docker already installed, skipping."
fi

# Install SDKMAN for Java and Maven
export SDKMAN_DIR="/media/abhijit/9E46A14F46A128CB/.sdkman"
if [ ! -d "$SDKMAN_DIR" ]; then
    echo "[3/6] Installing SDKMAN..."
    curl -s "https://get.sdkman.io" | bash
else
    echo "[3/6] SDKMAN already installed, skipping."
fi

[[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"

echo "[4/6] Installing Java 25 and Maven 3.9.6 via SDKMAN..."
# Java 25 Temurin (or openjdk fallback)
sdk install java 25-tem || sdk install java 25-open || echo "Warning: Java 25 installation failed or it is already installed."
sdk default java 25-tem || sdk default java 25-open || true
sdk install maven 3.9.6 || echo "Warning: Maven 3.9.6 installation failed or it is already installed."
sdk default maven 3.9.6 || true

# Install NVM for Node 20
export NVM_DIR="/media/abhijit/9E46A14F46A128CB/.nvm"
if [ ! -d "$NVM_DIR" ]; then
    echo "[5/6] Installing NVM..."
    mkdir -p "$NVM_DIR"
    curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
else
    echo "[5/6] NVM already installed, skipping."
fi

[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"

echo "[6/6] Installing Node.js 20..."
nvm install 20
nvm use 20
nvm alias default 20

# Create .env from example if it doesn't exist
if [ -f ".env.example" ] && [ ! -f ".env" ]; then
    echo "Copying .env.example to .env..."
    cp .env.example .env
fi

echo "========================================"
echo " Setup Complete!"
echo "========================================"
echo "Please restart your terminal or run:"
echo "source ~/.bashrc"
echo "to ensure all environment variables (sdkman, nvm) are loaded."
