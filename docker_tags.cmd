@echo off
chcp 65001 > nul
title Docker Registry Checker

echo [%date% %time%] Проверка Docker Registry на api.ars:5000
echo ================================================
echo.

echo [1/2] Запрос списка репозиториев...
echo --------------------------------
curl -s http://api.ars:5000/v2/_catalog
if %errorlevel% neq 0 (
    echo [ОШИБКА] Не удалось получить список репозиториев
) else (
    echo [OK] Список получен
)
echo.
echo --------------------------------

echo [2/2] Запрос тегов для oracle-api...
echo --------------------------------
curl -s http://api.ars:5000/v2/oracle-api/tags/list
if %errorlevel% neq 0 (
    echo [ОШИБКА] Не удалось получить теги
) else (
    echo [OK] Теги получены
)
echo.
echo --------------------------------

echo.
echo Проверка завершена
pause