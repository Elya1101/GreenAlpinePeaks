#!/bin/bash

# Остановить и удалить старые контейнеры
echo "Stopping old containers..."
docker-compose down

# Собрать и запустить
echo "Building and starting containers..."
docker-compose up --build -d

# Проверить статус
echo "Checking status..."
docker-compose ps

# Показать логи бэкенда
echo "Showing backend logs..."
docker-compose logs backend

echo "Application is running!"
echo "Frontend: http://localhost:3000"
echo "Backend API: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui/index.html"