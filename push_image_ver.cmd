@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

set "GREEN=[32m"
set "YELLOW=[33m"
set "RED=[31m"
set "RESET=[0m"

set REGISTRY=oracle-rest-api.ars:5001
set IMAGE_NAME=oracle-api

cls
echo %GREEN%========================================%RESET%
echo %GREEN%    СБОРКА DOCKER ОБРАЗА%RESET%
echo %GREEN%========================================%RESET%
echo.

:: ============================================
:: 1. ПРОВЕРКА ЗАВИСИМОСТЕЙ
:: ============================================
echo %YELLOW%[1/5] Проверка зависимостей...%RESET%

where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Docker не найден!%RESET%
    pause
    exit /b 1
)

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Maven не найден!%RESET%
    pause
    exit /b 1
)

echo %GREEN%[OK] Все зависимости найдены%RESET%
echo.

:: ============================================
:: 2. ГЕНЕРАЦИЯ ВЕРСИИ
:: ============================================
echo %YELLOW%[2/5] Генерация версии...%RESET%

for /f %%i in ('git rev-list --count HEAD 2^>nul') do set VERSION_NUMBER=%%i
if not defined VERSION_NUMBER set VERSION_NUMBER=0
set FULL_VERSION=1.25.!VERSION_NUMBER!
echo %GREEN%Версия: %FULL_VERSION%%RESET%
echo.

:: ============================================
:: 3. СБОРКА JAR
:: ============================================
echo %YELLOW%[3/5] Сборка JAR...%RESET%

call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка Maven не удалась!%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] JAR собран%RESET%
echo.

:: ============================================
:: 4. СБОРКА DOCKER ОБРАЗА И PUSH
:: ============================================
echo %YELLOW%[4/5] Сборка Docker образа и push...%RESET%

docker build -t %IMAGE_NAME%:%FULL_VERSION% .
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка Docker образа не удалась!%RESET%
    pause
    exit /b 1
)

docker tag %IMAGE_NAME%:%FULL_VERSION% %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
docker push %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Push в registry не удался!%RESET%
    pause
    exit /b 1
)

echo %GREEN%[OK] Образ %FULL_VERSION% отправлен в registry%RESET%
echo.

:: ============================================
:: 5. ОЧИСТКА
:: ============================================
echo %YELLOW%[5/5] Очистка старых локальных образов...%RESET%
docker image prune -f
echo %GREEN%[OK] Очистка завершена%RESET%
echo.

:: ============================================
:: РЕЗУЛЬТАТ
:: ============================================
echo %GREEN%========================================%RESET%
echo %GREEN%        СБОРКА ЗАВЕРШЕНА!%RESET%
echo %GREEN%========================================%RESET%
echo.
echo Образ: %FULL_VERSION%
echo.
echo ============================================
echo ЗАПУСК НА СЕРВЕРЕ
echo ============================================
echo.
echo 1. Остановить старые контейнеры:
echo    docker stop oracle-dev oracle-prod-blue oracle-prod-green 2^>nul
echo    docker rm oracle-dev oracle-prod-blue oracle-prod-green 2^>nul
echo    Или через Web http://oracle-rest-api.ars:9000
echo.
echo 2. Запустить DEV (тестовая):
echo    docker run -d --name oracle-dev --restart=always -p 8099:8080 -e SPRING_PROFILES_ACTIVE=dev %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
echo.
echo 3. Запустить PROD BLUE (текущая рабочая):
echo    docker run -d --name oracle-prod-blue --restart=always -p 8090:8080 -e SPRING_PROFILES_ACTIVE=prod %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
echo.
echo    Перед запуском проверить свободные порты и запустить на нем. Например: 8092
echo 4. Запустить PROD GREEN (новая версия для теста):
echo    docker run -d --name oracle-prod-green --restart=always -p 8092:8080 -e SPRING_PROFILES_ACTIVE=prod %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
echo.
echo    После проверки green версии перенаправить запросы на нужный порт
echo    Изменить порт в nginx.conf (nano /etc/nginx/nginx.conf) server 127.0.0.1:8092;  # Внутренний порт контейнера (blue)
echo    Проверить config: nginx -t
echo    Перечитать config: systemctl reload nginx
echo    Остановить blue: docker stop oracle-prod-blue
echo    Удалить blue: docker rm oracle-prod-blue или через Web http://oracle-rest-api.ars:9000
echo    Переименовать: docker rename oracle-prod-green oracle-prod-blue
echo.
echo
echo    Если удалить старые image в Docker REGISTRY Web http://oracle-rest-api.ars:7000
echo    Место очистится по задаче в cron или вручную запустить сборщик мусора
echo    cd /docker/registry
echo    docker compose exec registry registry garbage-collect --delete-untagged /etc/docker/registry/config.yml
echo.
pause