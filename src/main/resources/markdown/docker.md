# Docker

## Остановить контейнер
### docker stop oracle-app

## Удалить контейнер
### docker rm oracle-app

## Перезапустить с новым паролем
### docker run -d -p 8080:8080 -e SPRING_DATASOURCE_PASSWORD=новый_пароль --name oracle-app oracle-api

## Посмотреть переменные окружения в контейнере
### docker exec oracle-app env | grep SPRING

## Зайти в контейнер (для отладки)
### docker exec -it oracle-app sh