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
echo %GREEN%    СБОРКА DOCKER ОБРАЗА И ДОКУМЕНТАЦИИ%RESET%
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

set FULL_VERSION=1.25.%VERSION_NUMBER%
set /a APP_PORT=8000 + VERSION_NUMBER
set CONTAINER_NAME=oracle-prod-%VERSION_NUMBER%

echo %GREEN%Версия: %FULL_VERSION%%RESET%
echo %GREEN%Имя контейнера: %CONTAINER_NAME%%RESET%
echo %GREEN%Порт: %APP_PORT%%RESET%
echo.

:: ============================================
:: 3. ГЕНЕРАЦИЯ ДОКУМЕНТАЦИИ И СБОРКА JAR
:: ============================================
echo %YELLOW%[3/5] Генерация документации Dokka и сборка JAR...%RESET%

:: Генерация HTML-документации по коду (KDoc)
call mvn dokka:dokka
if exist target\dokka (
    xcopy /E /I /Y target\dokka src\main\resources\static\docs >nul
    echo %GREEN%[OK] Документация KDoc успешно встроена в ресурсы%RESET%
) else (
    echo %YELLOW%[ПРЕДУПРЕЖДЕНИЕ] Папка target\dokka не найдена, пропускаем копирование документации%RESET%
)

:: Сборка проекта и тестов (включая ArchUnit)
call mvn clean package
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
echo %GREEN%========================================%RESET%
echo %GREEN%        СБОРКА ЗАВЕРШЕНА!%RESET%
echo %GREEN%========================================%RESET%
echo.
echo Версия: %FULL_VERSION%
echo Порт: %APP_PORT%
echo Имя контейнера: %CONTAINER_NAME%
echo.
echo ============================================
echo ЗАПУСК И ОБНОВЛЕНИЕ НА СЕРВЕРЕ
echo ============================================
echo.
echo 1. Запустить DEV (тестовая среда на порту 8099):
echo    docker stop oracle-dev 2^>nul
echo    docker rm oracle-dev 2^>nul
echo    docker run -d --name oracle-dev --restart=always -p 8099:8080 -e SPRING_PROFILES_ACTIVE=dev %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
echo.
echo 2. Запустить новый PROD контейнер (%CONTAINER_NAME% на порту %APP_PORT%):
echo    docker run -d --name %CONTAINER_NAME% --restart=always -p %APP_PORT%:8080 -e SPRING_PROFILES_ACTIVE=prod %REGISTRY%/%IMAGE_NAME%:%FULL_VERSION%
echo.
echo 3. Обновить порт в Nginx:
echo    nano /etc/nginx/nginx.conf
echo    (Измените строку server 127.0.0.1:[старый_порт]; на server 127.0.0.1:%APP_PORT%;)
echo.
echo 4. Применить конфигурацию Nginx:
echo    nginx -t
echo    systemctl reload nginx
echo.
echo 5. Остановить и удалить старый PROD контейнер:
echo    docker stop oracle-prod-[старый_номер]
echo    docker rm oracle-prod-[старый_номер]
echo.
echo 6. Очистка места в Docker Registry (если удаляли старые образы через Web http://oracle-rest-api.ars:7000):
echo    docker exec -it registry-registry-1 bin/registry garbage-collect --delete-untagged /etc/docker/registry/config.yml
echo.
pause