#!/bin/bash

# Load environment variables
source .env

# Build the image
docker-compose build

# Publish the image to Docker Hub with version tag
docker push ${DOCKER_USERNAME}/deals-tg-bot:${BOT_VERSION}

# Also tag as latest for backward compatibility
docker tag ${DOCKER_USERNAME}/deals-tg-bot:${BOT_VERSION} ${DOCKER_USERNAME}/deals-tg-bot:latest
docker push ${DOCKER_USERNAME}/deals-tg-bot:latest 