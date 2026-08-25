@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

set "GREEN=[32m"
set "YELLOW=[33m"
set "RED=[31m"
set "RESET=[0m"

echo %GREEN%========================================%RESET%
echo %GREEN%    ГЕНЕРАЦИЯ KDOC ДОКУМЕНТАЦИИ%RESET%
echo %GREEN%========================================%RESET%
echo.

echo %YELLOW%[1/2] Запуск генератора Dokka...%RESET%
call mvn dokka:dokka
if %errorlevel% neq 0 (
    echo %RED%[ОШИБКА] Ошибка генерации Dokka!%RESET%
    pause
    exit /b 1
)

echo %YELLOW%[2/2] Копирование в ресурсы Spring Boot...%RESET%
if exist target\dokka (
    xcopy /E /I /Y target\dokka src\main\resources\static\docs >nul
    echo %GREEN%[OK] Документация успешно обновлена в src\main\resources\static\docs%RESET%
) else (
    echo %RED%[ОШИБКА] Папка target\dokka не найдена!%RESET%
    pause
    exit /b 1
)

echo.
echo %GREEN%========================================%RESET%
echo %GREEN%        ДОКУМЕНТАЦИЯ ОБНОВЛЕНА!%RESET%
echo %GREEN%========================================%RESET%
echo Теперь вы можете запустить проект локально или собрать релиз через push_image_ver.cmd
echo.
pause