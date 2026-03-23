@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

:: ============================================
:: НАСТРОЙКИ
:: ============================================
set CONTAINER_NAME=oracle-app
set REGISTRY_SERVER=api.ars
set REGISTRY_PORT=5000
set HOST_PORT=8080
set CONTAINER_PORT=8080
set IMAGE_NAME=oracle-api
set DB_PASSWORD=htrhtfwbz

cls
echo ========================================
echo    ЗАПУСК КОНТЕЙНЕРА %CONTAINER_NAME%
echo         ИЗ REGISTRY %REGISTRY_SERVER%:%REGISTRY_PORT%
echo ========================================
echo.

:: ============================================
:: ЭТАП 1: ПРОВЕРКА REGISTRY И ПОКАЗ ВЕРСИЙ
:: ============================================
echo ЭТАП 1: Проверка Registry %REGISTRY_SERVER%:%REGISTRY_PORT%
echo.

:: Проверка доступности
curl -f http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/ >nul 2>&1
if %errorlevel% neq 0 (
    echo [ОШИБКА] Registry %REGISTRY_SERVER%:%REGISTRY_PORT% недоступен!
    echo.
    echo Проверьте:
    echo   - Доступен ли сервер %REGISTRY_SERVER%
    echo   - Открыт ли порт %REGISTRY_PORT%
    echo   - Запущен ли контейнер registry
    pause
    exit /b 1
)
echo [OK] Registry доступен
echo.

:: Показываем все репозитории
echo Репозитории в Registry:
echo ----------------------------------------
curl -s http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/_catalog
echo ----------------------------------------
echo.

:: Показываем версии oracle-api
echo Версии %IMAGE_NAME% в Registry:
echo ----------------------------------------
curl -s http://%REGISTRY_SERVER%:%REGISTRY_PORT%/v2/%IMAGE_NAME%/tags/list
echo.
echo ----------------------------------------
echo.

:: ============================================
:: ЭТАП 2: ВЫБОР ВЕРСИИ
:: ============================================
echo ЭТАП 2: Выбор версии для запуска
echo.
echo Совет: Скопируйте нужную версию из списка выше
set /p VERSION="Введите версию (например latest): "

if "!VERSION!"=="" set VERSION=latest
echo.
echo Выбрана версия: !VERSION!
echo.

:: ============================================
:: ЭТАП 3: ПОДГОТОВКА К ЗАПУСКУ
:: ============================================
echo ЭТАП 3: Подготовка к запуску
echo.

:: Формируем полное имя образа в Registry
set FULL_IMAGE_NAME=%REGISTRY_SERVER%:%REGISTRY_PORT%/%IMAGE_NAME%:!VERSION!
echo Полное имя образа: !FULL_IMAGE_NAME!
echo.

:: Удаляем старый контейнер (принудительно)
echo Удаление старого контейнера %CONTAINER_NAME%...
docker rm -f %CONTAINER_NAME% 2>nul
echo [OK] Старый контейнер удален
echo.

:: Удаляем старую версию образа из локального кэша (оба тега, если есть)
echo Очистка локального кэша...
docker rmi %IMAGE_NAME%:!VERSION! 2>nul
docker rmi !FULL_IMAGE_NAME! 2>nul
echo [OK] Кэш очищен
echo.

:: ============================================
:: ЭТАП 4: ЗАПУСК КОНТЕЙНЕРА ИЗ REGISTRY
:: ============================================
echo ЭТАП 4: Запуск контейнера напрямую из Registry
echo.

:: Запускаем контейнер (образ загрузится автоматически при запуске)
echo Запуск контейнера из !FULL_IMAGE_NAME!...
echo Параметры:
echo   - Порт: %HOST_PORT%:%CONTAINER_PORT%
echo   - Пароль БД: %DB_PASSWORD%
echo.

docker run -d ^
  -p %HOST_PORT%:%CONTAINER_PORT% ^
  -e SPRING_DATASOURCE_PASSWORD=%DB_PASSWORD% ^
  --name %CONTAINER_NAME% ^
  !FULL_IMAGE_NAME!

:: Проверка результата
if %errorlevel% equ 0 (
    echo.
    echo [✓] КОНТЕЙНЕР УСПЕШНО ЗАПУЩЕН!
    echo.
    echo Информация о запущенном контейнере:
    echo ----------------------------------------
    docker ps --filter "name=%CONTAINER_NAME%" --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
    echo ----------------------------------------
    echo.
    echo Ссылки:
    echo   Приложение: http://localhost:%HOST_PORT%
    echo   Логи:       docker logs -f %CONTAINER_NAME%
    echo   Остановка:  docker stop %CONTAINER_NAME%
    echo   Удаление:   docker rm %CONTAINER_NAME%
    echo.
    echo Проверка образа в локальном кэше:
    docker images !FULL_IMAGE_NAME! --format "table {{.Repository}}:{{.Tag}}\t{{.ID}}\t{{.Size}}"
) else (
    echo [ОШИБКА] Не удалось запустить контейнер!
)

echo.
pause