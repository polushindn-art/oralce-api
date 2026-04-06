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
set REGISTRY=api.ars:5000
set IMAGE_NAME=oracle-api
set VERSION_PREFIX=1.25

cls
echo %GREEN%========================================%RESET%
echo %GREEN%    СБОРКА И ОТПРАВКА В REGISTRY%RESET%
echo %GREEN%========================================%RESET%
echo.
echo %YELLOW%Выберите тип сборки:%RESET%
echo.
echo 1. ТЕСТОВАЯ сборка (обновит тег: test)
echo 2. ПРОДАКШЕН сборка (обновит теги: latest, версия)
echo.
set /p BUILD_TYPE="Выберите (1/2): "

if "!BUILD_TYPE!"=="1" (
    set SPRING_PROFILE=dev
    set ENV_NAME=ТЕСТОВАЯ
    set IS_PROD=0
) else if "!BUILD_TYPE!"=="2" (
    set SPRING_PROFILE=prod
    set ENV_NAME=ПРОДАКШЕН
    set IS_PROD=1
) else (
    echo %RED%Неверный выбор!%RESET%
    pause
    exit /b 1
)

echo.
echo %GREEN%Выбрана %ENV_NAME% сборка%RESET%
echo   Профиль: %SPRING_PROFILE%
if "%IS_PROD%"=="1" (
    echo   Теги: latest, %VERSION_PREFIX%.XXX
) else (
    echo   Тег: test
)
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
:: ПРОВЕРКА REGISTRY
:: ============================================
echo %YELLOW%[2/6] Проверка Registry %REGISTRY%...%RESET%

curl -f http://%REGISTRY%/v2/ >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Registry %REGISTRY% недоступен!%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Registry доступен%RESET%
echo.

:: ============================================
:: ГЕНЕРАЦИЯ ВЕРСИИ (только для продакшена)
:: ============================================
if "%IS_PROD%"=="1" (
    echo %YELLOW%[3/6] Генерация версии...%RESET%
    
    git rev-parse --git-dir >nul 2>&1
    if %errorlevel% neq 0 (
        echo %RED%Не Git репозиторий%RESET%
        set /p VERSION_NUMBER="Введите номер версии: "
        if "!VERSION_NUMBER!"=="" set VERSION_NUMBER=1
    ) else (
        for /f %%i in ('git rev-list --count HEAD 2^>nul') do set VERSION_NUMBER=%%i
        if not defined VERSION_NUMBER set VERSION_NUMBER=0
        for /f %%i in ('git rev-parse --short HEAD 2^>nul') do set COMMIT_HASH=%%i
        echo   Коммитов: !VERSION_NUMBER!
        echo   Хэш: !COMMIT_HASH!
    )
    
    set FULL_VERSION=%VERSION_PREFIX%.!VERSION_NUMBER!
    echo %GREEN%Версия: %FULL_VERSION%%RESET%
    echo.
) else (
    echo %YELLOW%[3/6] Пропуск генерации версии (только для продакшена)%RESET%
    echo.
)

:: ============================================
:: MAVEN СБОРКА
:: ============================================
echo %YELLOW%[4/6] Сборка Maven с профилем %SPRING_PROFILE%...%RESET%

if not exist "pom.xml" (
    echo %RED%[ОШИБКА] Файл pom.xml не найден!%RESET%
    pause
    exit /b 1
)

call mvn clean package -DskipTests -P%SPRING_PROFILE%

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка Maven не удалась!%RESET%
    pause
    exit /b 1
)

echo %GREEN%[OK] Maven сборка завершена%RESET%
echo.

:: ============================================
:: DOCKER СБОРКА
:: ============================================
echo %YELLOW%[5/6] Сборка Docker образа...%RESET%

if "%IS_PROD%"=="1" (
    docker build -t %IMAGE_NAME%:%FULL_VERSION% .
    set LOCAL_TAG=%FULL_VERSION%
) else (
    docker build -t %IMAGE_NAME%:test .
    set LOCAL_TAG=test
)

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка Docker образа не удалась!%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Docker образ собран: %LOCAL_TAG%%RESET%
echo.

:: ============================================
:: ОТПРАВКА В REGISTRY
:: ============================================
echo %YELLOW%[6/6] Отправка в Registry...%RESET%

if "%IS_PROD%"=="1" (
    :: Продакшен: отправляем версию и latest
    echo Отправка версии %FULL_VERSION%...
    docker tag %IMAGE_NAME%:%FULL_VERSION% %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
    docker push %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
    
    echo Отправка тега latest...
    docker tag %IMAGE_NAME%:%FULL_VERSION% %REGISTRY%/%IMAGE_NAME%:latest
    docker push %REGISTRY%/%IMAGE_NAME%:latest
) else (
    :: Тест: отправляем только test
    echo Отправка тега test...
    docker tag %IMAGE_NAME%:test %REGISTRY%/%IMAGE_NAME%:test
    docker push %REGISTRY%/%IMAGE_NAME%:test
)

echo %GREEN%[OK] Все образы отправлены%RESET%
echo.

:: ============================================
:: РЕЗУЛЬТАТ
:: ============================================
echo %GREEN%========================================%RESET%
echo %GREEN%        СБОРКА ЗАВЕРШЕНА!%RESET%
echo %GREEN%========================================%RESET%
echo.
if "%IS_PROD%"=="1" (
    echo Окружение: ПРОДАКШЕН
    echo Версия: %FULL_VERSION%
    echo.
    echo Отправленные теги:
    echo   - %FULL_VERSION% (конкретная версия)
    echo   - latest (последняя продакшен)
) else (
    echo Окружение: ТЕСТОВАЯ
    echo.
    echo Отправленный тег:
    echo   - test (последняя тестовая)
)
echo.
echo Просмотр всех тегов в Registry:
echo   curl http://%REGISTRY%/v2/%IMAGE_NAME%/tags/list
echo.
pause