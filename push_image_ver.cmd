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

:: === НАСТРОЙКА ПРЕФИКСА ВЕРСИИ ===
:: Это число будет стоять перед точкой (например 1.6, 1.7, 2.0 и т.д.)
set VERSION_PREFIX=1.25
:: =================================

:: Проверка наличия Docker
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Docker не найден! Установите Docker Desktop%RESET%
    pause
    exit /b 1
)

:: Проверка наличия Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Maven не найден! Установите Maven или используйте Eclipse/Terminal с Maven%RESET%
    pause
    exit /b 1
)

:: Проверка доступности Registry
echo %YELLOW%[1/8] Проверка доступности Registry %REGISTRY%...%RESET%
curl -f http://%REGISTRY%/v2/ >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Registry %REGISTRY% недоступен!%RESET%
    echo Проверьте:
    echo   1. Запущен ли контейнер registry на api.ars
    echo   2. Открыт ли порт 5000 в брандмауэре
    echo   3. Настроен ли insecure-registries в Docker
    pause
    exit /b 1
)
echo %GREEN%[OK] Registry доступен%RESET%
echo.

:: Выбор действия
echo %YELLOW%[2/8] Выбор действия%RESET%
echo.
echo 1. Собрать проект Maven и Docker образ
echo 2. Использовать существующий локальный образ
echo.
set /p ACTION="Выберите действие (1/2): "

if "!ACTION!"=="2" goto use_existing

:: ========== КОМПИЛЯЦИЯ MAVEN ==========
echo.
echo %YELLOW%[3/8] Компиляция проекта Maven...%RESET%

:: Проверяем наличие pom.xml
if not exist "pom.xml" (
    echo %RED%[ОШИБКА] Файл pom.xml не найден в текущей директории!%RESET%
    pause
    exit /b 1
)

:: Запускаем Maven сборку
echo Запуск mvn clean package...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка Maven не удалась! Проверьте ошибки выше.%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Проект успешно скомпилирован%RESET%

:: Проверяем наличие JAR файла
echo Проверка JAR файла...
dir target\*.jar >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] JAR файл не найден в папке target!%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] JAR файл найден%RESET%
echo.

:: ========== СБОРКА DOCKER ОБРАЗА ==========
echo %YELLOW%[4/8] Сборка Docker образа...%RESET%
docker build -t %IMAGE_NAME%:latest .

if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Сборка Docker образа не удалась%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Docker образ собран%RESET%
echo.

set LOCAL_TAG=latest
goto continue

:use_existing
echo.
echo %YELLOW%[3/8] Выбор существующего образа%RESET%
echo.
echo Доступные локальные образы %IMAGE_NAME%:
docker images %IMAGE_NAME% --format "table {{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
echo.

set /p LOCAL_TAG="Введите тег существующего образа (например latest): "
if "!LOCAL_TAG!"=="" set LOCAL_TAG=latest
echo.

:continue
:: ========== ГЕНЕРАЦИЯ ВЕРСИИ С ПРЕФИКСОМ ==========
echo %YELLOW%[5/8] Генерация версии...%RESET%
echo.

:: Проверяем, что мы в Git репозитории
git rev-parse --git-dir >nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Это не Git репозиторий!%RESET%
    echo Пожалуйста, инициализируйте Git или введите версию вручную
    set /p CUSTOM_VERSION="Введите версию вручную (например 1.6): "
    if "!CUSTOM_VERSION!"=="" pause & exit /b 1
    set REMOTE_TAG=!CUSTOM_VERSION!
    goto continue_version
)

:: Получаем количество коммитов
for /f %%i in ('git rev-list --count HEAD 2^>nul') do set COMMIT_COUNT=%%i
if not defined COMMIT_COUNT set COMMIT_COUNT=0

:: Получаем хэш для информации
for /f %%i in ('git rev-parse --short HEAD 2^>nul') do set COMMIT_HASH=%%i

:: Формируем версию: ПРЕФИКС.КОММИТЫ
set REMOTE_TAG=%VERSION_PREFIX%.!COMMIT_COUNT!

echo %GREEN%Информация о репозитории:%RESET%
echo   Префикс версии: %VERSION_PREFIX%
echo   Коммитов:       !COMMIT_COUNT!
echo   Хэш:           !COMMIT_HASH!
echo.
echo %GREEN%Сгенерированная версия: %REMOTE_TAG%%RESET%
echo.

:: Спрашиваем подтверждение
set /p CONFIRM="Использовать эту версию? (y/n - ввести вручную): "
if /i not "!CONFIRM!"=="y" (
    set /p CUSTOM_VERSION="Введите версию вручную (например 1.6): "
    if "!CUSTOM_VERSION!"=="" pause & exit /b 1
    set REMOTE_TAG=!CUSTOM_VERSION!
)

:continue_version
echo %GREEN%[OK] Версия установлена: %REMOTE_TAG%%RESET%
echo.

:: Проверка insecure-registries
echo.
echo %YELLOW%[6/8] Проверка настроек Docker...%RESET%
docker system info | findstr "%REGISTRY%" >nul
if %errorlevel% neq 0 (
    echo %RED%[ПРЕДУПРЕЖДЕНИЕ] %REGISTRY% не найден в insecure-registries!%RESET%
    echo.
    echo Добавьте в настройки Docker Engine:
    echo {
    echo   "insecure-registries": ["%REGISTRY%"]
    echo }
    echo.
    set /p CONTINUE="Продолжить все равно? (y/n): "
    if /i not "!CONTINUE!"=="y" exit /b 1
)
echo %GREEN%[OK] Настройки проверены%RESET%

:: Тэгирование
echo.
echo %YELLOW%[7/8] Тэгирование образа...%RESET%
docker tag %IMAGE_NAME%:%LOCAL_TAG% %REGISTRY%/%IMAGE_NAME%:%REMOTE_TAG%
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Не удалось затэгировать образ%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Образ затэгирован как %REMOTE_TAG%%RESET%

:: Отправка
echo.
echo %YELLOW%[8/8] Отправка версии %REMOTE_TAG% в Registry...%RESET%
docker push %REGISTRY%/%IMAGE_NAME%:%REMOTE_TAG%
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Не удалось отправить образ%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Версия %REMOTE_TAG% отправлена%RESET%

:: Опционально - отправка как latest
echo.
set /p UPDATE_LATEST="Обновить тег latest в Registry? (y/n): "
if /i "!UPDATE_LATEST!"=="y" (
    echo %YELLOW%Обновление тега latest...%RESET%
    docker tag %IMAGE_NAME%:%LOCAL_TAG% %REGISTRY%/%IMAGE_NAME%:latest
    docker push %REGISTRY%/%IMAGE_NAME%:latest
    echo %GREEN%[OK] Тег latest обновлен%RESET%
)

:: Проверка результата
echo.
echo %YELLOW%Проверка загруженных версий...%RESET%
echo.
echo Список репозиториев в Registry:
curl -s http://%REGISTRY%/v2/_catalog
echo.
echo.
echo Теги для %IMAGE_NAME%:
curl -s http://%REGISTRY%/v2/%IMAGE_NAME%/tags/list
echo.

echo.
echo %GREEN%========================================%RESET%
echo %GREEN%        ОПЕРАЦИЯ УСПЕШНО ЗАВЕРШЕНА     %RESET%
echo %GREEN%========================================%RESET%
echo.
echo Отправленная версия: %REMOTE_TAG%
echo.
echo Примеры использования на других компьютерах:
echo   docker pull %REGISTRY%/%IMAGE_NAME%:%REMOTE_TAG%
if /i "!UPDATE_LATEST!"=="y" echo   docker pull %REGISTRY%/%IMAGE_NAME%:latest
echo.
pause