# Инструкция по сборке и публикации Docker образа

## 1. Подготовка окружения

1. Убедитесь, что у вас установлены необходимые инструменты:
```bash
# Проверка версии Docker
docker --version

# Проверка версии Docker Compose
docker-compose --version
```

2. Создайте аккаунт на Docker Hub, если у вас его еще нет:
   - Перейдите на https://hub.docker.com/
   - Зарегистрируйтесь и создайте аккаунт
   - Запомните ваш username

## 2. Настройка проекта

1. Клонируйте репозиторий:
```bash
git clone https://github.com/your-username/deals-tg-bot.git
cd deals-tg-bot
```

2. Создайте файл `.env` и заполните необходимые переменные:
```bash
# Создаем файл .env
touch .env
```

3. Откройте файл `.env` и добавьте следующие строки:
```bash
BOT_USERNAME=your_bot_username
BOT_TOKEN=your_bot_token
DOCKER_USERNAME=your_dockerhub_username
BOT_VERSION=1.0.0
```
Замените:
- `your_bot_username` на имя вашего бота в Telegram
- `your_bot_token` на токен бота, полученный от @BotFather
- `your_dockerhub_username` на ваш username в Docker Hub

## 3. Сборка проекта

1. Соберите проект с помощью Maven:
```bash
./mvnw clean package -DskipTests
```

2. Соберите Docker образ:
```bash
docker-compose build
```

3. Проверьте, что образ создан:
```bash
docker images | grep deals-tg-bot
```

## 4. Публикация на Docker Hub

1. Войдите в Docker Hub:
```bash
docker login
```
Введите ваш username и password от Docker Hub.

2. Убедитесь, что скрипт публикации имеет права на выполнение:
```bash
chmod +x publish.sh
```

3. Запустите скрипт публикации:
```bash
./publish.sh
```

4. Проверьте, что образ опубликован:
```bash
docker pull ${DOCKER_USERNAME}/deals-tg-bot:${BOT_VERSION}
```

## 5. Проверка работоспособности

1. Создайте тестовую директорию:
```bash
mkdir test-bot
cd test-bot
```

2. Создайте файл `.env` с необходимыми переменными:
```bash
BOT_USERNAME=your_bot_username
BOT_TOKEN=your_bot_token
BOT_VERSION=1.0.0
```

3. Создайте `docker-compose.yml`:
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

4. Запустите контейнер:
```bash
docker-compose up -d
```

5. Проверьте логи:
```bash
docker-compose logs -f
```

## 6. Обновление версии

При необходимости обновить версию:

1. Измените версию в файле `.env`:
```bash
BOT_VERSION=1.0.1
```

2. Внесите необходимые изменения в код

3. Соберите и опубликуйте новую версию:
```bash
./publish.sh
```

## Локальное тестирование бота

### 1. Настройка локального окружения

1. Убедитесь, что у вас есть доступ к интернету для работы с Telegram API

2. Создайте файл `.env` в корневой директории проекта:
```properties
# Настройки бота
BOT_USERNAME=your_bot_username
BOT_TOKEN=your_bot_token

# Настройки Docker (опционально, для публикации)
DOCKER_USERNAME=your_dockerhub_username
BOT_VERSION=1.0.0
```

3. Создайте файл `application.properties` в директории `src/main/resources/`:
```properties
# Настройки для Long Polling
bot.username=${BOT_USERNAME}
bot.token=${BOT_TOKEN}
```

### 2. Запуск бота локально

1. Создайте директорию для логов:
```bash
mkdir -p logs
```

2. Соберите проект:
```bash
./mvnw clean package -DskipTests
```

3. Запустите приложение:
```bash
java -jar target/dealsTGBot-0.0.1-SNAPSHOT.jar
```

### 3. Проверка работоспособности

1. Откройте Telegram и найдите вашего бота
2. Отправьте команду `/start`
3. Проверьте логи приложения:
```bash
tail -f logs/bot.log
```

### 4. Отладка

1. Проверьте статус бота:
```bash
curl https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getMe
```

2. Проверьте логи приложения:
```bash
tail -f logs/bot.log
```

### 5. Полезные команды для отладки

```bash
# Проверка статуса приложения
ps aux | grep dealsTGBot

# Проверка логов в реальном времени
tail -f logs/bot.log

# Перезапуск приложения
kill -9 $(pgrep -f dealsTGBot)
java -jar target/dealsTGBot-0.0.1-SNAPSHOT.jar
```

### 6. Рекомендации по настройке

1. Используйте systemd для управления процессом (Linux):
```ini
[Unit]
Description=Deals Telegram Bot
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/bot
ExecStart=/usr/bin/java -jar target/dealsTGBot-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

2. Настройте автоматический перезапуск при сбоях
3. Настройте ротацию логов
4. Используйте мониторинг системы (например, Prometheus + Grafana)

### 7. Преимущества использования Long Polling для тестирования

1. Не требует SSL-сертификата
2. Не нужен статический IP-адрес
3. Не требует настройки webhook
4. Проще в отладке
5. Работает за NAT
6. Не требует открытых портов на сервере

### 8. Ограничения Long Polling

1. Немного большая задержка по сравнению с webhook
2. Больше нагрузка на сервер Telegram
3. Не подходит для высоконагруженных ботов

### 9. Возможные проблемы и их решение

1. Бот не отвечает:
   - Проверьте логи в `logs/bot.log`
   - Убедитесь, что токен бота указан правильно
   - Проверьте подключение к интернету

2. Команды не работают:
   - Проверьте формат ввода команд
   - Убедитесь, что бот запущен и работает
   - Проверьте логи на наличие ошибок

3. Проблемы с подключением:
   - Проверьте токен бота
   - Убедитесь, что имя бота указано правильно
   - Проверьте доступ к интернету

## Возможные проблемы и их решение

1. Если возникает ошибка при сборке:
```bash
# Очистите Docker кэш
docker system prune -a

# Пересоберите образ
docker-compose build --no-cache
```

2. Если не удается опубликовать образ:
```bash
# Проверьте, что вы вошли в Docker Hub
docker login

# Проверьте права доступа к репозиторию
```

3. Если контейнер не запускается:
```bash
# Проверьте логи
docker-compose logs

# Проверьте переменные окружения
docker-compose config
```

## Рекомендации по безопасности

1. Никогда не публикуйте файл `.env` в репозиторий
2. Регулярно обновляйте токен бота
3. Используйте последние версии Docker и Docker Compose
4. Регулярно обновляйте базовые образы для исправления уязвимостей

## Структура версий

Проект использует семантическое версионирование (MAJOR.MINOR.PATCH):
- MAJOR версия для несовместимых изменений API
- MINOR версия для добавления функциональности с сохранением обратной совместимости
- PATCH версия для исправления ошибок с сохранением обратной совместимости

Доступные версии:
- `latest` - Последняя стабильная версия
- `1.0.0` - Первый стабильный релиз 