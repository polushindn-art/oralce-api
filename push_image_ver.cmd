@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

:: Цветной вывод
set "GREEN=[32m"
set "YELLOW=[33m"
set "RED=[31m"
set "RESET=[0m"

:: Настройки
set REGISTRY=api.ars:5000
set IMAGE_NAME=oracle-api
set VERSION_PREFIX=1.25

cls
echo %GREEN%========================================%RESET%
echo %GREEN%    СБОРКА ВСЕХ ОБРАЗОВ%RESET%
echo %GREEN%========================================%RESET%
echo.

:: ============================================
:: ПРОВЕРКА ЗАВИСИМОСТЕЙ
:: ============================================
echo %YELLOW%[1/6] Проверка зависимостей...%RESET%

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
:: ГЕНЕРАЦИЯ ВЕРСИИ ДЛЯ PROD
:: ============================================
echo %YELLOW%[2/6] Генерация версии для PROD...%RESET%

for /f %%i in ('git rev-list --count HEAD 2^>nul') do set VERSION_NUMBER=%%i
if not defined VERSION_NUMBER set VERSION_NUMBER=0
set FULL_VERSION=%VERSION_PREFIX%.!VERSION_NUMBER!
echo %GREEN%Версия PROD: %FULL_VERSION%%RESET%
echo.

:: ============================================
:: СБОРКА TEST (dev)
:: ============================================
echo %YELLOW%[3/6] Сборка TEST образа (dev)...%RESET%

call mvn clean package -DskipTests -Pdev

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка TEST не удалась!%RESET%
    pause
    exit /b 1
)

docker build -t %IMAGE_NAME%:test .
docker tag %IMAGE_NAME%:test %REGISTRY%/%IMAGE_NAME%:test
docker push %REGISTRY%/%IMAGE_NAME%:test

echo %GREEN%[OK] TEST образ отправлен%RESET%
echo.

:: ============================================
:: СБОРКА PROD
:: ============================================
echo %YELLOW%[4/6] Сборка PROD образа (prod)...%RESET%

call mvn clean package -DskipTests -Pprod

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка PROD не удалась!%RESET%
    pause
    exit /b 1
)

docker build -t %IMAGE_NAME%:%FULL_VERSION% .
docker tag %IMAGE_NAME%:%FULL_VERSION% %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
docker push %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%

docker tag %IMAGE_NAME%:%FULL_VERSION% %REGISTRY%/%IMAGE_NAME%:latest
docker push %REGISTRY%/%IMAGE_NAME%:latest

echo %GREEN%[OK] PROD образ отправлен%RESET%
echo.

:: ============================================
:: ПРОВЕРКА
:: ============================================
echo %YELLOW%[5/6] Проверка Registry...%RESET%
echo.
echo Теги для %IMAGE_NAME%:
curl -s http://%REGISTRY%/v2/%IMAGE_NAME%/tags/list
echo.
echo.

:: ============================================
:: РЕЗУЛЬТАТ
:: ============================================
echo %GREEN%========================================%RESET%
echo %GREEN%        СБОРКА ЗАВЕРШЕНА!%RESET%
echo %GREEN%========================================%RESET%
echo.
echo Отправленные образы:
echo   - test (последняя тестовая)
echo   - latest (последняя продакшен)
echo   - %FULL_VERSION% (конкретная продакшен)
echo.
echo Запуск:
echo   TEST: docker run -d -p 8081:8080 -e SPRING_PROFILES_ACTIVE=dev %REGISTRY%/%IMAGE_NAME%:test
echo   PROD: docker run -d -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod %REGISTRY%/%IMAGE_NAME%:latest
echo.
pause