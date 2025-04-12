# Telegram Bot for Making Bets

A bot for creating and managing bets in Telegram. Allows users to create bets, join them as participants or judges, and make decisions.

## Features

- Create bets with title, description, start date, and end date
- View bets by ID
- Ability to join a bet as a participant or judge
- Automatic ID generation for each bet

## Requirements

- Docker
- Docker Compose
- Telegram Bot Token (get it from [@BotFather](https://t.me/botfather))

## Quick Start

1. Clone the repository:
```bash
git clone https://github.com/your-username/deals-tg-bot.git
cd deals-tg-bot
```

2. Create `.env` file and fill in the required variables:
```bash
BOT_USERNAME=your_bot_username
BOT_TOKEN=your_bot_token
DOCKER_USERNAME=your_dockerhub_username
BOT_VERSION=1.0.0
```

3. Start the bot:
```bash
docker-compose up -d
```

## Using Docker Hub

### Running from Docker Hub

1. Create a project directory and navigate to it:
```bash
mkdir deals-tg-bot
cd deals-tg-bot
```

2. Create `.env` file with required variables:
```bash
BOT_USERNAME=your_bot_username
BOT_TOKEN=your_bot_token
BOT_VERSION=1.0.0
```

3. Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  bot:
    image: your_dockerhub_username/deals-tg-bot:${BOT_VERSION:-1.0.0}
    container_name: deals-tg-bot
    restart: always
    environment:
      - BOT_USERNAME=${BOT_USERNAME}
      - BOT_TOKEN=${BOT_TOKEN}
    volumes:
      - ./logs:/app/logs
```

4. Start the container:
```bash
docker-compose up -d
```

### Versioning

The bot uses semantic versioning (MAJOR.MINOR.PATCH):
- MAJOR version for incompatible API changes
- MINOR version for backwards-compatible functionality additions
- PATCH version for backwards-compatible bug fixes

Available versions:
- `latest` - Latest stable version
- `1.0.0` - Initial stable release

### Publishing to Docker Hub

1. Login to Docker Hub:
```bash
docker login
```

2. Update the version in `.env` file if needed:
```bash
BOT_VERSION=1.0.1
```

3. Run the publish script:
```bash
./publish.sh
```

## Using the Bot

1. Open Telegram and find your bot by username
2. Send the `/start` command
3. Use menu buttons to:
   - Create a new bet
   - View existing bet by ID
   - Join a bet as a participant or judge

## Logging

Bot logs are saved in the `logs/` directory and are available inside the container at `/app/logs`.

## Development

### Local Development

1. Clone the repository
2. Create `.env` file
3. Build and start the container:
```bash
docker-compose up -d --build
```

### Building the Image

```bash
docker-compose build
```

## Security

- Do not commit `.env` file to the repository
- Regularly update the bot token
- Use the latest versions of Docker and Docker Compose

## License

MIT

## Support

Create an issue in the project repository if you encounter any problems. 