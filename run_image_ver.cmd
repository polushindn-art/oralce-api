@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

:: ============================================
:: ЦВЕТА (работают в Windows 10/11)
:: ============================================
:: Включаем поддержку ANSI
reg query HKCU\Console /v VirtualTerminalLevel >nul 2>&1
if %errorlevel% neq 0 (
    reg add HKCU\Console /v VirtualTerminalLevel /t REG_DWORD /d 1 /f >nul 2>&1
)

set "GREEN="
set "YELLOW="
set "RED="
set "RESET="

:: Простая цветная печать через powershell
set "GREEN_C=92m"
set "YELLOW_C=93m"
set "RED_C=91m"
set "RESET_C=0m"

:: ============================================
:: НАСТРОЙКИ
:: ============================================
set COMPOSE_DIR=C:\OracleAPI\docker
set COMPOSE_FILE=%COMPOSE_DIR%\docker-compose.yml

cls
echo ========================================
echo     УПРАВЛЕНИЕ ЧЕРЕЗ DOCKER COMPOSE
echo ========================================
echo.
echo Выберите действие:
echo.
echo 1. Запустить ВСЁ (registry + dev + prod)
echo 2. Запустить только DEV (тестовая, порт 8081)
echo 3. Запустить только PROD (рабочая, порт 8080)
echo 4. Запустить только Registry (порт 5000)
echo 5. Остановить ВСЁ
echo 6. Перезапустить PROD (после обновления образа)
echo 7. Перезапустить DEV (после обновления образа)
echo 8. Посмотреть логи (всех сервисов)
echo 9. Очистить старые образы в registry (GC)
echo.
set /p "ACTION=Выберите (1-9): "

:: Переходим в папку с docker-compose.yml
cd /d "%COMPOSE_DIR%" 2>nul
if %errorlevel% neq 0 (
    echo [ОШИБКА] Папка %COMPOSE_DIR% не найдена!
    pause
    exit /b 1
)

:: ============================================
:: ДЕЙСТВИЯ
:: ============================================

if "%ACTION%"=="1" (
    echo.
    echo Запуск ВСЕХ сервисов...
    docker compose up -d
    echo.
    echo [OK] Запущено:
    echo   - Registry:  http://localhost:5000
    echo   - DEV:       http://localhost:8081
    echo   - PROD:      http://localhost:8080
    goto :end
)

if "%ACTION%"=="2" (
    echo.
    echo Запуск DEV окружения (тестовая)...
    docker compose up -d app-dev
    echo.
    echo [OK] DEV приложение запущено на http://localhost:8081
    goto :end
)

if "%ACTION%"=="3" (
    echo.
    echo Запуск PROD окружения (рабочая)...
    docker compose up -d app-prod
    echo.
    echo [OK] PROD приложение запущено на http://localhost:8080
    goto :end
)

if "%ACTION%"=="4" (
    echo.
    echo Запуск Registry...
    docker compose up -d registry
    echo.
    echo [OK] Registry запущен на http://localhost:5000
    goto :end
)

if "%ACTION%"=="5" (
    echo.
    echo Остановка ВСЕХ сервисов...
    docker compose down
    echo.
    echo [OK] Все сервисы остановлены
    goto :end
)

if "%ACTION%"=="6" (
    echo.
    echo Обновление PROD из registry...
    echo.
    echo [1/2] Скачивание нового образа...
    docker compose pull app-prod
    echo.
    echo [2/2] Перезапуск контейнера...
    docker compose up -d app-prod
    echo.
    echo [OK] PROD обновлён и перезапущен
    goto :end
)

if "%ACTION%"=="7" (
    echo.
    echo Обновление DEV из registry...
    echo.
    echo [1/2] Скачивание нового образа...
    docker compose pull app-dev
    echo.
    echo [2/2] Перезапуск контейнера...
    docker compose up -d app-dev
    echo.
    echo [OK] DEV обновлён и перезапущен
    goto :end
)

if "%ACTION%"=="8" (
    echo.
    echo Логи (Ctrl+C для выхода)...
    echo.
    docker compose logs -f
    goto :end
)

if "%ACTION%"=="9" (
    echo.
    echo Очистка старых образов в registry...
    docker exec docker-registry bin/registry garbage-collect /etc/docker/registry/config.yml
    echo.
    echo [OK] Очистка выполнена
    goto :end
)

echo.
echo [ОШИБКА] Неверный выбор!

:end
echo.
pause