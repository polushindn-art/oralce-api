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
set REGISTRY_SERVER=api.ars
set REGISTRY_PORT=5000
set IMAGE_NAME=oracle-api
set CONTAINER_PORT=8080
set DB_PASSWORD=htrhtfwbz

cls
echo %GREEN%========================================%RESET%
echo %GREEN%    ЗАПУСК КОНТЕЙНЕРА ИЗ REGISTRY%RESET%
echo %GREEN%========================================%RESET%
echo.
echo %YELLOW%Выберите окружение для запуска:%RESET%
echo.
echo 1. ТЕСТОВАЯ среда (тег: test, порт: 8081, профиль: dev)
echo 2. ПРОДАКШЕН (тег: latest, порт: 8080, профиль: prod)
echo.
set /p ENV="Выберите (1/2): "

if "!ENV!"=="1" (
    set TAG=test
    set CONTAINER_NAME=oracle-app-test
    set HOST_PORT=8081
    set SPRING_PROFILE=dev
    set ENV_NAME=ТЕСТОВАЯ
) else if "!ENV!"=="2" (
    set TAG=latest
    set CONTAINER_NAME=oracle-app-prod
    set HOST_PORT=8080
    set SPRING_PROFILE=prod
    set ENV_NAME=ПРОДАКШЕН
) else (
    echo %RED%Неверный выбор!%RESET%
    pause
    exit /b 1
)

set FULL_IMAGE_NAME=%REGISTRY_SERVER%:%REGISTRY_PORT%/%IMAGE_NAME%:%TAG%

echo.
echo %GREEN%========================================%RESET%
echo %GREEN%   Запуск %ENV_NAME% окружения%RESET%
echo %GREEN%========================================%RESET%
echo.
echo   Контейнер: %CONTAINER_NAME%
echo   Порт: %HOST_PORT%:%CONTAINER_PORT%
echo   Тег: %TAG%
echo   Профиль: %SPRING_PROFILE%
echo   Образ: %FULL_IMAGE_NAME%
echo.

:: ============================================
:: ЭТАП 1: ПРОВЕРКА REGISTRY
:: ============================================
echo %YELLOW%[1/4] Проверка Registry %REGISTRY_SERVER%:%REGISTRY_PORT%...%RESET%

curl -f http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/ >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Registry недоступен!%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Registry доступен%RESET%
echo.

:: ============================================
:: ЭТАП 2: ПРОВЕРКА НАЛИЧИЯ ОБРАЗА
:: ============================================
echo %YELLOW%[2/4] Проверка образа %IMAGE_NAME%:%TAG% в Registry...%RESET%

curl -s http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/%IMAGE_NAME%/tags/list | findstr "\"%TAG%\"" >nul
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Образ с тегом %TAG% не найден в Registry!%RESET%
    echo.
    echo Доступные теги:
    curl -s http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/%IMAGE_NAME%/tags/list
    echo.
    pause
    exit /b 1
)
echo %GREEN%[OK] Образ найден: %FULL_IMAGE_NAME%%RESET%
echo.

:: ============================================
:: ЭТАП 3: ОСТАНОВКА И УДАЛЕНИЕ СТАРОГО КОНТЕЙНЕРА
:: ============================================
echo %YELLOW%[3/4] Остановка и удаление старого контейнера %CONTAINER_NAME%...%RESET%

docker stop %CONTAINER_NAME% 2>nul
docker rm %CONTAINER_NAME% 2>nul
echo %GREEN%[OK] Старый контейнер удален%RESET%
echo.

:: ============================================
:: ЭТАП 4: ЗАПУСК НОВОГО КОНТЕЙНЕРА
:: ============================================
echo %YELLOW%[4/4] Запуск контейнера из %FULL_IMAGE_NAME%...%RESET%

docker run -d ^
  --name %CONTAINER_NAME% ^
  -p %HOST_PORT%:%CONTAINER_PORT% ^
  -e SPRING_PROFILES_ACTIVE=%SPRING_PROFILE% ^
  -e SPRING_DATASOURCE_PASSWORD=%DB_PASSWORD% ^
  --restart unless-stopped ^
  %FULL_IMAGE_NAME%

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Не удалось запустить контейнер!%RESET%
    pause
    exit /b 1
)

timeout /t 3 /nobreak >nul

echo.
echo %GREEN%========================================%RESET%
echo %GREEN%        КОНТЕЙНЕР ЗАПУЩЕН!%RESET%
echo %GREEN%========================================%RESET%
echo.
docker ps --filter "name=%CONTAINER_NAME%" --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
echo.
echo %GREEN%Ссылки:%RESET%
echo   Приложение: http://localhost:%HOST_PORT%
echo   Логи:       docker logs -f %CONTAINER_NAME%
echo   Остановка:  docker stop %CONTAINER_NAME%
echo   Удаление:   docker rm %CONTAINER_NAME%
echo.
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
set REGISTRY_SERVER=api.ars
set REGISTRY_PORT=5000
set IMAGE_NAME=oracle-api
set CONTAINER_PORT=8080
set DB_PASSWORD=htrhtfwbz

cls
echo %GREEN%========================================%RESET%
echo %GREEN%    ЗАПУСК КОНТЕЙНЕРА ИЗ REGISTRY%RESET%
echo %GREEN%========================================%RESET%
echo.
echo %YELLOW%Выберите окружение для запуска:%RESET%
echo.
echo 1. ТЕСТОВАЯ среда (тег: test, порт: 8081, профиль: dev)
echo 2. ПРОДАКШЕН (тег: latest, порт: 8080, профиль: prod)
echo.
set /p ENV="Выберите (1/2): "

if "!ENV!"=="1" (
    set TAG=test
    set CONTAINER_NAME=oracle-app-test
    set HOST_PORT=8081
    set SPRING_PROFILE=dev
    set ENV_NAME=ТЕСТОВАЯ
) else if "!ENV!"=="2" (
    set TAG=latest
    set CONTAINER_NAME=oracle-app-prod
    set HOST_PORT=8080
    set SPRING_PROFILE=prod
    set ENV_NAME=ПРОДАКШЕН
) else (
    echo %RED%Неверный выбор!%RESET%
    pause
    exit /b 1
)

set FULL_IMAGE_NAME=%REGISTRY_SERVER%:%REGISTRY_PORT%/%IMAGE_NAME%:%TAG%

echo.
echo %GREEN%========================================%RESET%
echo %GREEN%   Запуск %ENV_NAME% окружения%RESET%
echo %GREEN%========================================%RESET%
echo.
echo   Контейнер: %CONTAINER_NAME%
echo   Порт: %HOST_PORT%:%CONTAINER_PORT%
echo   Тег: %TAG%
echo   Профиль: %SPRING_PROFILE%
echo   Образ: %FULL_IMAGE_NAME%
echo.

:: ============================================
:: ЭТАП 1: ПРОВЕРКА REGISTRY
:: ============================================
echo %YELLOW%[1/4] Проверка Registry %REGISTRY_SERVER%:%REGISTRY_PORT%...%RESET%

curl -f http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/ >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Registry недоступен!%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Registry доступен%RESET%
echo.

:: ============================================
:: ЭТАП 2: ПРОВЕРКА НАЛИЧИЯ ОБРАЗА
:: ============================================
echo %YELLOW%[2/4] Проверка образа %IMAGE_NAME%:%TAG% в Registry...%RESET%

curl -s http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/%IMAGE_NAME%/tags/list | findstr "\"%TAG%\"" >nul
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Образ с тегом %TAG% не найден в Registry!%RESET%
    echo.
    echo Доступные теги:
    curl -s http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/%IMAGE_NAME%/tags/list
    echo.
    pause
    exit /b 1
)
echo %GREEN%[OK] Образ найден: %FULL_IMAGE_NAME%%RESET%
echo.

:: ============================================
:: ЭТАП 3: ОСТАНОВКА И УДАЛЕНИЕ СТАРОГО КОНТЕЙНЕРА
:: ============================================
echo %YELLOW%[3/4] Остановка и удаление старого контейнера %CONTAINER_NAME%...%RESET%

docker stop %CONTAINER_NAME% 2>nul
docker rm %CONTAINER_NAME% 2>nul
echo %GREEN%[OK] Старый контейнер удален%RESET%
echo.

:: ============================================
:: ЭТАП 4: ЗАПУСК НОВОГО КОНТЕЙНЕРА
:: ============================================
echo %YELLOW%[4/4] Запуск контейнера из %FULL_IMAGE_NAME%...%RESET%

docker run -d ^
  --name %CONTAINER_NAME% ^
  -p %HOST_PORT%:%CONTAINER_PORT% ^
  -e SPRING_PROFILES_ACTIVE=%SPRING_PROFILE% ^
  -e SPRING_DATASOURCE_PASSWORD=%DB_PASSWORD% ^
  --restart unless-stopped ^
  %FULL_IMAGE_NAME%

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Не удалось запустить контейнер!%RESET%
    pause
    exit /b 1
)

timeout /t 3 /nobreak >nul

echo.
echo %GREEN%========================================%RESET%
echo %GREEN%        КОНТЕЙНЕР ЗАПУЩЕН!%RESET%
echo %GREEN%========================================%RESET%
echo.
docker ps --filter "name=%CONTAINER_NAME%" --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
echo.
echo %GREEN%Ссылки:%RESET%
echo   Приложение: http://localhost:%HOST_PORT%
echo   Логи:       docker logs -f %CONTAINER_NAME%
echo   Остановка:  docker stop %CONTAINER_NAME%
echo   Удаление:   docker rm %CONTAINER_NAME%
echo.
pause