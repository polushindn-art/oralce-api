@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

:: ============================================
:: ЦВЕТА
:: ============================================
set "GREEN=[32m"
set "YELLOW=[33m"
set "RED=[31m"
set "RESET=[0m"

:: ============================================
:: НАСТРОЙКИ
:: ============================================
set COMPOSE_DIR=C:\OracleAPI\docker
set COMPOSE_FILE=%COMPOSE_DIR%\docker-compose.yml

cls
echo %GREEN%========================================%RESET%
echo %GREEN%    УПРАВЛЕНИЕ ЧЕРЕЗ DOCKER COMPOSE%RESET%
echo %GREEN%========================================%RESET%
echo.
echo %YELLOW%Выберите действие:%RESET%
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
set /p ACTION="Выберите (1-9): "

:: Переходим в папку с docker-compose.yml
cd /d "%COMPOSE_DIR%" 2>nul
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Папка %COMPOSE_DIR% не найдена!%RESET%
    pause
    exit /b 1
)

:: ============================================
:: ДЕЙСТВИЯ
:: ============================================

if "!ACTION!"=="1" (
    echo.
    echo %YELLOW%Запуск ВСЕХ сервисов...%RESET%
    docker compose up -d
    echo.
    echo %GREEN%[OK] Запущено:%RESET%
    echo   - Registry:  http://localhost:5000
    echo   - DEV:       http://localhost:8081
    echo   - PROD:      http://localhost:8080
    goto :end
)

if "!ACTION!"=="2" (
    echo.
    echo %YELLOW%Запуск DEV окружения (тестовая)...%RESET%
    docker compose up -d app-dev
    echo.
    echo %GREEN%[OK] DEV приложение запущено на http://localhost:8081%RESET%
    goto :end
)

if "!ACTION!"=="3" (
    echo.
    echo %YELLOW%Запуск PROD окружения (рабочая)...%RESET%
    docker compose up -d app-prod
    echo.
    echo %GREEN%[OK] PROD приложение запущено на http://localhost:8080%RESET%
    goto :end
)

if "!ACTION!"=="4" (
    echo.
    echo %YELLOW%Запуск Registry...%RESET%
    docker compose up -d registry
    echo.
    echo %GREEN%[OK] Registry запущен на http://localhost:5000%RESET%
    goto :end
)

if "!ACTION!"=="5" (
    echo.
    echo %YELLOW%Остановка ВСЕХ сервисов...%RESET%
    docker compose down
    echo.
    echo %GREEN%[OK] Все сервисы остановлены%RESET%
    goto :end
)

if "!ACTION!"=="6" (
    echo.
    echo %YELLOW%Обновление PROD из registry...%RESET%
    echo.
    echo %YELLOW%[1/2] Скачивание нового образа...%RESET%
    docker compose pull app-prod
    echo.
    echo %YELLOW%[2/2] Перезапуск контейнера...%RESET%
    docker compose up -d app-prod
    echo.
    echo %GREEN%[OK] PROD обновлён и перезапущен%RESET%
    goto :end
)

if "!ACTION!"=="7" (
    echo.
    echo %YELLOW%Обновление DEV из registry...%RESET%
    echo.
    echo %YELLOW%[1/2] Скачивание нового образа...%RESET%
    docker compose pull app-dev
    echo.
    echo %YELLOW%[2/2] Перезапуск контейнера...%RESET%
    docker compose up -d app-dev
    echo.
    echo %GREEN%[OK] DEV обновлён и перезапущен%RESET%
    goto :end
)

if "!ACTION!"=="8" (
    echo.
    echo %YELLOW%Логи (Ctrl+C для выхода)...%RESET%
    echo.
    docker compose logs -f
    goto :end
)

if "!ACTION!"=="9" (
    echo.
    echo %YELLOW%Очистка старых образов в registry...%RESET%
    docker exec docker-registry bin/registry garbage-collect /etc/docker/registry/config.yml
    echo.
    echo %GREEN%[OK] Очистка выполнена%RESET%
    goto :end
)

echo.
echo %RED%Неверный выбор!%RESET%

:end
echo.
pause