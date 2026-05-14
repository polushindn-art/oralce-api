# Сборка проекта


## Собрать и отправить в Dcoker Registry
### Запустить .\push_image_ver.cmd


# ЗАПУСК НА СЕРВЕРЕ


## 1. Остановить старые контейнеры:
###   docker stop oracle-dev oracle-prod-blue oracle-prod-green 2>nul
###   docker rm oracle-dev oracle-prod-blue oracle-prod-green 2>nul
###   Или через Web http://oracle-rest-api.ars:9000

## 2. Запустить DEV (тестовая):
###   docker run -d --name oracle-dev --restart=always -p 8099:8080 -e SPRING_PROFILES_ACTIVE=dev oracle-rest-api.ars:5001/oracle-api:1.25.47

## 3. Запустить PROD BLUE (текущая рабочая):
###   docker run -d --name oracle-prod-blue --restart=always -p 8090:8080 -e SPRING_PROFILES_ACTIVE=prod oracle-rest-api.ars:5001/oracle-api:1.25.47

###  Перед запуском проверить свободные порты и запустить на нем. Например: 8092
## 4. Запустить PROD GREEN (новая версия для теста):
###    docker run -d --name oracle-prod-green --restart=always -p 8092:8080 -e SPRING_PROFILES_ACTIVE=prod oracle-rest-api.ars:5001/oracle-api:1.25.47

### После проверки green версии перенаправить запросы на нужный порт
### Изменить порт в nginx.conf (nano /etc/nginx/nginx.conf) server 127.0.0.1:8092;  # Внутренний порт контейнера (blue)
### Проверить config: nginx -t
### Перечитать config: systemctl reload nginx
### Остановить blue: docker stop oracle-prod-blue
### Удалить blue: docker rm oracle-prod-blue или через Web http://oracle-rest-api.ars:9000
### Переименовать: docker rename oracle-prod-green oracle-prod-blue