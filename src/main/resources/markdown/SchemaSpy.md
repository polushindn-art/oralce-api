# Полная инструкция по развертыванию, автообновлению и интеграции SchemaSpy

## 1. Подготовка файлов проекта
Создайте папку (например, `C:\schemaspy-docker`) и положите в неё только два файла:
1. `schemaspy-app.jar` (файл SchemaSpy)
2. `ojdbc6.jar` (драйвер Oracle)

---

## 2. Создание файла `.dockerignore`
Создайте файл `.dockerignore` и добавьте в него:
output_docs/

---

## 3. Создание файла `Dockerfile`
(Учитывает часовой пояс Барнаул UTC+7, добавление плашки времени в index.html, создание файла updated.txt и поддержку CORS для удаленных серверов):

    FROM eclipse-temurin:21-jre-alpine

    RUN apk add --no-cache graphviz python3

    WORKDIR /app

    COPY schemaspy-app.jar schemaspy.jar
    COPY ojdbc6.jar ojdbc6.jar

    EXPOSE 8080

    ENV DB_HOST="qrw.ars" \
        DB_PORT="1521" \
        DB_NAME="QR10" \
        DB_USER="qreal" \
        DB_PASS="quickie" \
        DB_SCHEMA="QREAL"

    CMD ["sh", "-c", "java -jar schemaspy.jar -t orathin -dp ojdbc6.jar -host \"$DB_HOST\" -port \"$DB_PORT\" -db \"$DB_NAME\" -u \"$DB_USER\" -p \"$DB_PASS\" -s \"$DB_SCHEMA\" -cat % -o /app/output_docs -noimplied && python3 -c 'import re, datetime; barnaul_tz = datetime.timezone(datetime.timedelta(hours=7)); now = datetime.datetime.now(barnaul_tz).strftime(\"%Y-%m-%d %H:%M:%S\"); open(\"/app/output_docs/updated.txt\", \"w\", encoding=\"utf-8\").write(now); path=\"/app/output_docs/index.html\"; f=open(path, \"r\", encoding=\"utf-8\", errors=\"ignore\"); content=f.read(); f.close(); badge=f\"<div style=\\\"background:#1e293b;color:#38bdf8;padding:12px;text-align:center;font-family:sans-serif;font-size:14px;border-bottom:3px solid #22c55e;position:relative;z-index:9999;\\\"><i style=\\\"font-style:normal;\\\">🕒</i> Автоматическое обновление документации (Барнаул): <b>{now}</b></div>\"; content=re.sub(r\"(<body[^>]*>)\", r\"\\1\" + badge, content, count=1, flags=re.IGNORECASE); f=open(path, \"w\", encoding=\"utf-8\"); f.write(content); f.close()' && cd /app/output_docs && python3 -c 'import http.server, socketserver; h = type(\"H\", (http.server.SimpleHTTPRequestHandler,), {\"end_headers\": lambda self: (self.send_header(\"Access-Control-Allow-Origin\", \"*\"), http.server.SimpleHTTPRequestHandler.end_headers(self))}); socketserver.TCPServer((\"\", 8080), h).serve_forever()'"]

---

## 4. Сборка и отправка в Registry (вручную)
Выполните в терминале (меняйте цифру `:1` на `:2`, `:3` при обновлениях):

    docker build -t schemaspy .
    docker tag schemaspy oracle-rest-api.ars:5001/schemaspy:1
    docker push oracle-rest-api.ars:5001/schemaspy:1

---

## 5. Запуск на сервере
Подключитесь к серверу и запустите контейнер в фоновом режиме на порту **8070**:

    docker run -d --name schemaspy-docs --restart=always -p 8070:8080 oracle-rest-api.ars:5001/schemaspy:1

---

## 6. Настройка автообновления через Cron (каждый день в 7:00 по Барнаулу)
Чтобы контейнер ежедневно перезапускался, пересобирал актуальную структуру базы и обновлял время:

1. Подключитесь к серверу по SSH.
2. Откройте планировщик задач:

   crontab -e

3. Добавьте в самый конец файла строку:

   0 7 * * * docker restart schemaspy-docs

⚠️ **Важно:** В конце файла обязательно нажмите `Enter`, чтобы оставить пустую строку перед закрытием (иначе Cron выдаст ошибку `missing newline before EOF`).
4. Сохраните файл (`Ctrl + O`, `Enter`, `Ctrl + X`).

---

## 7. Отображение времени обновления на главном дашборде (в сайдбаре)
Вставьте этот код в левую панель вашего основного Thymeleaf-шаблона в блок **«Инфраструктура»**:

    <div style="padding: 10px 15px; background: #fdf4ff; border: 1px solid #f5d0fe; border-radius: 8px; font-size: 12px;">
        <div style="font-weight: bold; color: #86198f; margin-bottom: 4px;">🗄️ Структура БД (SchemaSpy):</div>
        <div style="color: #64748b; margin-bottom: 4px;" id="schemaspy-sync-time">⏳ Проверка времени...</div>
        <a href="http://oracle-rest-api.ars:8070" target="_blank" style="display: inline-block; color: #2563eb; text-decoration: none; font-weight: bold;">Открыть документацию ↗</a>
    </div>

    <script>
        async function loadSchemaSpyUpdateTime() {
            const timeEl = document.getElementById('schemaspy-sync-time');
            try {
                const response = await fetch('http://oracle-rest-api.ars:8070/updated.txt', { cache: 'no-cache' });
                if (response.ok) {
                    const updateTime = await response.text();
                    timeEl.innerHTML = `🔄 Обновлено: <b style="color: #1e293b;">${updateTime.trim()} (Барнаул)</b>`;
                } else {
                    timeEl.textContent = '⚠️ Не удалось получить время';
                }
            } catch (e) {
                timeEl.textContent = '🔴 Сервер SchemaSpy недоступен';
            }
        }
        loadSchemaSpyUpdateTime();
        setInterval(loadSchemaSpyUpdateTime, 30000);
    </script>