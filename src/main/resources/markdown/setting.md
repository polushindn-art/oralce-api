# Инструкция для программиста: настройка Git и IDEA для работы с русскими буквами

## 1. Настройка Git

### 1.1. Отключить экранирование не-ASCII символов

git config --global core.quotepath false

Что это даёт:
- В git status и git log будут отображаться нормальные русские буквы
- В консоли не будет кракозябр

---

### 1.2. Настроить кодировку для коммитов

git config --global i18n.commitEncoding utf-8
git config --global i18n.logOutputEncoding utf-8

Что это даёт:
- Сообщения коммитов с русскими буквами будут корректно сохраняться и отображаться

---

### 1.3. Настройка для Windows

git config --global core.autocrlf false

---

## 2. Настройка IntelliJ IDEA

### 2.1. Кодировка файлов

File → Settings → Editor → File Encodings

Установить:
- Global Encoding: UTF-8
- Project Encoding: UTF-8
- Default encoding for properties files: UTF-8

Поставить галочку: Transparent native-to-ascii conversion

---

### 2.2. Кодировка консоли при запуске приложения

Run → Edit Configurations → VM options

Добавить:

-Dfile.encoding=UTF-8
-Dconsole.encoding=UTF-8

---

### 2.3. Если в терминале IDEA отображаются кракозябры

Выполнить в терминале:

chcp 65001

Или добавить в Help → Edit Custom VM Options:

-Dfile.encoding=UTF-8
-Dconsole.encoding=UTF-8

---

## 3. Настройка системы (Windows)

### 3.1. Включить поддержку UTF-8

1. Панель управления → Региональные стандарты
2. Вкладка Дополнительно
3. Поставить галочку: Использовать Юникод (UTF-8) для поддержки языка во всей системе
4. Перезагрузить компьютер

---

### 3.2. Настройка консоли

Для корректного отображения русских букв:

chcp 65001

Чтобы это было постоянно:
1. Открыть PowerShell от имени администратора
2. Выполнить:
   Set-ItemProperty -Path "HKLM:\SYSTEM\CurrentControlSet\Control\Nls\CodePage" -Name "OEMCP" -Value "65001"
   Set-ItemProperty -Path "HKLM:\SYSTEM\CurrentControlSet\Control\Nls\CodePage" -Name "ACP" -Value "65001"
3. Перезагрузить компьютер

---

## 4. Настройка Maven (если используется)

В pom.xml добавить:

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
</properties>

---

## 5. Проверка настроек

### 5.1. Проверить Git

git config --global --list | findstr utf

Должно быть:
core.quotepath=false
i18n.commitEncoding=utf-8
i18n.logOutputEncoding=utf-8

---

### 5.2. Проверить IDEA

Создайте файл с русским именем:
1. File → New → File
2. Введите: Тест.md
3. Нажмите Enter
4. Файл должен отображаться нормально

---

### 5.3. Проверить консоль

git log --oneline

Если русские буквы отображаются нормально — всё настроено правильно.

---

## 6. Что делать, если проблема осталась

1. Перезапустить IDEA
2. Перезапустить консоль
3. Перезагрузить компьютер

---

## 7. Быстрый чек-лист

- [ ] core.quotepath = false
- [ ] i18n.commitEncoding = utf-8
- [ ] i18n.logOutputEncoding = utf-8
- [ ] IDEA: Global Encoding = UTF-8
- [ ] IDEA: Project Encoding = UTF-8
- [ ] IDEA: Transparent native-to-ascii conversion = включена
- [ ] Система: Использовать UTF-8 = включено
- [ ] Перезагрузка выполнена

---

## 8. Пример

До настройки:
[main 39db227] ╨а╤Г╤Б╤Б╨║╨╕╨╡ ╨▒╤Г╨║╨▓╤Л

После настройки:
[main 39db227] Русские буквы