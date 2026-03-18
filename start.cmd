@echo off
chcp 65001 > nul
echo Запуск Spring Boot в кодировке UTF-8...
call mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dfile.encoding=UTF-8"