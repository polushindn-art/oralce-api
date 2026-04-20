@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

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

if "%ACTION%"=="1" (
    echo Запуск ВСЕХ сервисов...
    docker compose up -d
    echo [OK] Запущено: registry (5000), dev (8081), prod (8080)
)
if "%ACTION%"=="2" (
    echo Запуск DEV...
    docker compose up -d app-dev
    echo [OK] DEV на http://localhost:8081
)
if "%ACTION%"=="3" (
    echo Запуск PROD...
    docker compose up -d app-prod
    echo [OK] PROD на http://localhost:8080
)
if "%ACTION%"=="4" (
    echo Запуск Registry...
    docker compose up -d registry
    echo [OK] Registry на http://localhost:5000
)
if "%ACTION%"=="5" (
    echo Остановка...
    docker compose down
    echo [OK] Остановлено
)
if "%ACTION%"=="6" (
    echo Обновление PROD...
    docker compose pull app-prod
    docker compose up -d app-prod
    echo [OK] PROD обновлён
)
if "%ACTION%"=="7" (
    echo Обновление DEV...
    docker compose pull app-dev
    docker compose up -d app-dev
    echo [OK] DEV обновлён
)
if "%ACTION%"=="8" (
    echo Логи (Ctrl+C для выхода)...
    docker compose logs -f
)
if "%ACTION%"=="9" (
    echo Очистка старых образов...
    docker exec docker-registry bin/registry garbage-collect /etc/docker/registry/config.yml
    echo [OK] Очистка выполнена
)
if "%ACTION%" lss "1" goto :error
if "%ACTION%" gtr "9" goto :error
goto :end

:error
echo [ОШИБКА] Неверный выбор: %ACTION%

:end
echo.
pause