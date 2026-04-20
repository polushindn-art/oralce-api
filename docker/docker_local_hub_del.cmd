@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

:: ===== НАСТРОЙКИ =====
set REGISTRY=localhost:5000
set REPO=oracle-api
set KEEP_COUNT=5
:: ====================

echo === Очистка репозитория %REPO% ===
echo Оставляем последние %KEEP_COUNT% версий
echo.

:: 1. Получаем список всех тегов
echo Получение списка тегов...
curl -s http://%REGISTRY%/v2/%REPO%/tags/list > %TEMP%\tags.json 2>nul

:: Проверяем, есть ли файл
if not exist %TEMP%\tags.json (
    echo Ошибка: не удалось получить список тегов
    echo Проверьте, что registry доступен по адресу %REGISTRY%
    exit /b 1
)

:: Используем jq для Windows (нужен в PATH)
where jq >nul 2>nul
if %errorlevel% neq 0 (
    echo Ошибка: jq не найден. Скачайте jq для Windows и добавьте в PATH
    echo https://stedolan.github.io/jq/download/
    exit /b 1
)

:: Получаем теги и сохраняем во временный файл
jq -r ".tags[]" %TEMP%\tags.json > %TEMP%\tags.txt 2>nul

:: Проверяем, есть ли теги
findstr /r "." %TEMP%\tags.txt >nul
if %errorlevel% neq 0 (
    echo Ошибка: нет тегов в репозитории
    del %TEMP%\tags.json %TEMP%\tags.txt 2>nul
    exit /b 1
)

:: 2. Исключаем защищённые теги
echo Обработка тегов...
findstr /v "latest test dev master main" %TEMP%\tags.txt > %TEMP%\tags_filtered.txt 2>nul

:: 3. Сортируем по версиям и оставляем последние KEEP_COUNT
:: Для Windows используем sort с опциями
type %TEMP%\tags_filtered.txt | sort > %TEMP%\tags_sorted.txt 2>nul

:: Подсчитываем количество тегов
set COUNT=0
for /f "usebackq delims=" %%i in ("%TEMP%\tags_sorted.txt") do set /a COUNT+=1

set /a KEEP=%KEEP_COUNT%
set /a DELETE_COUNT=%COUNT% - %KEEP%

:: Определяем теги для удаления (первые DELETE_COUNT строк)
set /a counter=0
if exist %TEMP%\tags_to_delete.txt del %TEMP%\tags_to_delete.txt
if exist %TEMP%\tags_to_keep.txt del %TEMP%\tags_to_keep.txt

for /f "usebackq delims=" %%i in ("%TEMP%\tags_sorted.txt") do (
    set /a counter+=1
    if !counter! leq %DELETE_COUNT% (
        echo %%i >> %TEMP%\tags_to_delete.txt
    ) else (
        echo %%i >> %TEMP%\tags_to_keep.txt
    )
)

:: 4. Показываем, что будем оставлять
echo.
echo [OK] Теги, которые останутся (последние %KEEP_COUNT%):
if exist %TEMP%\tags_to_keep.txt (
    for /f "usebackq delims=" %%i in ("%TEMP%\tags_to_keep.txt") do echo   - %%i
) else (
    echo   (нет)
)

:: 5. Показываем, что будем удалять
echo.
echo [WARN] Теги, которые будут удалены:
if exist %TEMP%\tags_to_delete.txt (
    for /f "usebackq delims=" %%i in ("%TEMP%\tags_to_delete.txt") do echo   - %%i
) else (
    echo   (нет тегов для удаления)
)

:: 6. Запрашиваем подтверждение
echo.
set /p CONFIRM="Продолжить удаление? (y/N): "
if /i not "!CONFIRM!"=="y" (
    echo Отменено.
    del %TEMP%\tags*.txt %TEMP%\tags.json 2>nul
    exit /b 0
)

:: 7. Удаляем теги
set DELETED_COUNT=0
set FAILED_COUNT=0

if exist %TEMP%\tags_to_delete.txt (
    for /f "usebackq delims=" %%i in ("%TEMP%\tags_to_delete.txt") do (
        set TAG=%%i
        echo Удаление !TAG!...
        
        :: Получаем digest
        curl -s -I -H "Accept: application/vnd.docker.distribution.manifest.v2+json" ^
            http://%REGISTRY%/v2/%REPO%/manifests/!TAG! > %TEMP%\headers.txt 2>nul
        
        :: Извлекаем digest из заголовка
        set DIGEST=
        for /f "tokens=2 delims=: " %%a in ('findstr /i "Docker-Content-Digest" %TEMP%\headers.txt') do set DIGEST=%%a
        
        if defined DIGEST (
            :: Удаляем
            curl -s -X DELETE http://%REGISTRY%/v2/%REPO%/manifests/%DIGEST% >nul 2>nul
            if !errorlevel! equ 0 (
                echo   [OK] Удален
                set /a DELETED_COUNT+=1
            ) else (
                echo   [FAIL] Ошибка при удалении
                set /a FAILED_COUNT+=1
            )
        ) else (
            echo   [FAIL] Не удалось получить digest
            set /a FAILED_COUNT+=1
        )
    )
)

:: 8. Сборка мусора
echo.
echo Запуск garbage collection...
docker exec registry bin/registry garbage-collect /etc/docker/registry/config.yml 2>nul

:: 9. Итог
echo.
echo === Готово ===
echo Удалено тегов: %DELETED_COUNT%
if %FAILED_COUNT% gtr 0 echo Ошибок: %FAILED_COUNT%

:: Очистка временных файлов
del %TEMP%\tags*.txt %TEMP%\tags.json %TEMP%\headers.txt 2>nul

pause