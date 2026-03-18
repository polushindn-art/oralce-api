@echo off
chcp 65001 > nul
echo Запуск пересборки в кодировке UTF-8...

echo 1. Останавливаем старый контейнер...
docker stop oracle-app 2>nul
docker rm oracle-app 2>nul

echo 2. Удаляем старый образ...
docker rmi oracle-api 2>nul

echo 3. Пересобираем JAR (без тестов)...
call mvn clean package -DskipTests

echo 4. Собираем новый образ (без кэша)...
docker build --no-cache -t oracle-api .

echo 5. Запускаем новый контейнер...
docker run -d -p 8080:8080 ^
  -e SPRING_DATASOURCE_PASSWORD=htrhtfwbz ^
  --name oracle-app ^
  oracle-api

echo 6. Готово! Смотрим логи:
docker logs -f oracle-app