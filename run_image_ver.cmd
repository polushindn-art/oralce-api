@echo off
chcp 65001 > nul
setlocal

set COMPOSE_DIR=C:\OracleAPI\docker

cls
echo ========================================
echo     УПРАВЛЕНИЕ ЧЕРЕЗ DOCKER COMPOSE
echo ========================================
echo.
echo 1. Запустить ВСЁ (registry + dev + prod)
echo 2. Запустить только DEV (порт 8081)
echo 3. Запустить только PROD (порт 8080)
echo 4. Запустить только Registry (порт 5000)
echo 5. Остановить ВСЁ
echo 6. Перезапустить PROD
echo 7. Перезапустить DEV
echo 8. Посмотреть логи
echo 9. Очистить старые образы
echo.
set /p ACTION="Выберите (1-9): "

cd /d "%COMPOSE_DIR%" 2>nul

goto :action_%ACTION% 2>nul

:action_1
echo Запуск ВСЕХ сервисов...
docker compose up -d
echo [OK] Запущено: registry (5000), dev (8081), prod (8080)
goto :end

:action_2
echo Запуск DEV...
docker compose up -d app-dev
echo [OK] DEV на http://localhost:8081
goto :end

:action_3
echo Запуск PROD...
docker compose up -d app-prod
echo [OK] PROD на http://localhost:8080
goto :end

:action_4
echo Запуск Registry...
docker compose up -d registry
echo [OK] Registry на http://localhost:5000
goto :end

:action_5
echo Остановка...
docker compose down
echo [OK] Остановлено
goto :end

:action_6
echo Обновление PROD...
docker compose pull app-prod
docker compose up -d app-prod
echo [OK] PROD обновлён
goto :end

:action_7
echo Обновление DEV...
docker compose pull app-dev
docker compose up -d app-dev
echo [OK] DEV обновлён
goto :end

:action_8
echo Логи (Ctrl+C для выхода)...
docker compose logs -f
goto :end

:action_9
echo Очистка старых образов...
docker exec docker-registry bin/registry garbage-collect /etc/docker/registry/config.yml
echo [OK] Очистка выполнена
goto :end

:end
echo.
pause