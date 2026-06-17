## Cхема приёмки товара (с offline-first)

---
### Схема 1. Подготовительная часть (Офис)

```mermaid
sequenceDiagram
    participant Поставщик
    participant Менеджер
    participant Товаровед
    participant 1С
    participant API as REST API
    participant Oracle as Oracle

    Note over Поставщик,1С: 1. Поступление документов
    
    Поставщик->>1С: УПД
    1С->>Товаровед: Задание на сопоставление
    
    Note over Товаровед,Менеджер: 2. Сопоставление номенклатуры
    
    loop Каждая позиция УПД
        Товаровед->>Товаровед: Товар есть в номенклатуре?
        
        alt Товар найден
            Товаровед->>1С: Привязка к существующей
        else Товар не найден
            Товаровед->>Менеджер: Запрос на создание
            Менеджер->>1С: Создание новой номенклатуры
            Товаровед->>1С: Привязка к новой
        end
    end
    
    Товаровед->>1С: Сопоставление завершено
    1С->>1С: Поступление товаров и услуг
    
    Note over 1С,Oracle: 3. Создание задания в WMS
    
    1С->>API: POST /v1/wms/create
    API->>Oracle: INSERT WMSHEAD (PENDING)
    Oracle-->>API: OK
    API-->>1С: 200 OK (wmshead_rn)
```
---
### Схема 2. Исполнительная часть (Склад)

```mermaid
sequenceDiagram
    participant API as REST API
    participant Oracle as Oracle
    participant OF as Openfire
    participant ТСД as ТСД (WakeUp)
    participant WMS as WMS Приложение
    participant Room as SQLite Room
    participant 1С

    Note over API,ТСД: 1. Пробуждение ТСД

    API->>OF: XMPP message (task_id)
    OF-->>ТСД: Push-уведомление

    ТСД->>API: POST /v1/wms/ack (status=received)
    API-->>ТСД: 200 OK

    ТСД->>WMS: Запуск WMS приложения (Intent)

    Note over WMS,Oracle: 2. Загрузка задания

    WMS->>API: GET /v1/wms/task/{id}
    API->>Oracle: SELECT WMSHEAD, WMSSPEC
    Oracle-->>API: Данные документа
    API-->>WMS: 200 OK (документ + спецификации)
    WMS->>Room: Сохранение в локальную БД
    WMS->>API: POST /v1/wms/ack (status=opened)
    API-->>WMS: 200 OK

    Note over WMS,Room: 3. Приёмка (офлайн)

    loop Каждый товар
        WMS->>WMS: Сканирование
        WMS->>Room: Сохранение сканирования
        WMS->>WMS: Отображение на экране
    end

    Note over WMS,Oracle: 4. Фоновая синхронизация

    WMS->>WMS: WorkManager запускает синхронизацию

    loop Отправка данных
        WMS->>API: POST /v1/wms/receive
        API->>Oracle: UPDATE WMSSPEC
        Oracle-->>API: OK
        API-->>WMS: 200 OK
        WMS->>Room: Обновление статуса (SYNCED)
    end

    Note over WMS,1С: 5. Завершение

    WMS->>API: POST /v1/wms/complete
    API->>Oracle: UPDATE status='COMPLETED'
    API->>Oracle: INSERT WMS_TO_1C (PENDING)

    alt Всё совпало
        API->>1С: WebSocket уведомление
        1С->>Oracle: SELECT WMSHEAD, WMSSPEC (из очереди)
        1С->>1С: Создание приходного ордера
        1С->>Oracle: UPDATE WMS_TO_1C (PROCESSED)
    else Есть расхождения
        API->>1С: WebSocket уведомление
        1С->>Oracle: SELECT WMSHEAD, WMSSPEC (из очереди)
        1С->>1С: Приходный ордер + ТОРГ-2
        1С->>Oracle: UPDATE WMS_TO_1C (PROCESSED)
    end

    API-->>WMS: 200 OK
    WMS->>Room: Очистка документа
    WMS->>WMS: "Приёмка завершена"
```
---

## Офлайн-синхронизация (Room)

```mermaid
flowchart LR
    subgraph Android
        UI[Экран ТСД]
        Room[(Room SQLite)]
        WM[WorkManager]
    end

    subgraph Сервер
        API[REST API]
        Oracle[(Oracle)]
    end

    UI -->|1. Сканирование| Room
    Room -->|2. Накопление| WM
    WM -->|3. POST /v1/wms/receive| API
    API -->|4. Запись| Oracle

    API -->|5. 200 OK| WM
    WM -->|6. Обновление статуса| Room
    Room -->|7. Отображение| UI
```

## Жизненный цикл данных на ТСД

```mermaid
stateDiagram-v2
    [*] --> Загрузка_с_Oracle
    Загрузка_с_Oracle --> Хранение_в_Room: Данные загружены
    
    Хранение_в_Room --> Работа_офлайн: Связь есть/нет
    
    Работа_офлайн --> Накопление_сканирований: Сканирование
    Накопление_сканирований --> Работа_офлайн: Продолжение работы
    
    Накопление_сканирований --> Синхронизация: Сеть восстановлена
    
    Синхронизация --> Отправка_в_Oracle: POST /receive
    Отправка_в_Oracle --> Обновление_Room: 200 OK
    
    Обновление_Room --> Работа_офлайн: Продолжение
    
    Синхронизация --> Завершение: Документ завершён
    Завершение --> Очистка_Room: Очистка локальных данных
    Очистка_Room --> [*]
```