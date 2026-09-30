# Магазин кормов для животных

Консольная информационная система для учёта покупателей и заказов зоомагазина.  
Данные хранятся в PostgreSQL. Архитектура: **Console UI → Service → Repository (JDBC) → PostgreSQL**.

## Требования

- Java 17
- Maven 3.x
- Docker (для PostgreSQL)

## Запуск

1. Поднять базу данных:

```bash
docker compose up -d
```

2. Собрать проект:

```bash
mvn clean compile
```

3. Запустить приложение:

```bash
mvn exec:java
```

Точка входа: `app.Main`.

## Структура проекта

```
src/main/java/
  app/Main.java                 — точка входа
  ui/ConsoleMenu.java           — консольное меню
  model/Buyer.java              — покупатель
  model/Order.java              — заказ
  model/OrderStatus.java        — статусы заказа
  repository/BuyerRepository.java      — интерфейс репозитория покупателей
  repository/JdbcBuyerRepository.java
  repository/OrderRepository.java      — интерфейс репозитория заказов
  repository/JdbcOrderRepository.java
  service/BuyerService.java     — бизнес-логика покупателей
  service/OrderService.java     — бизнес-логика заказов
  service/OrderStatistics.java  — DTO статистики
  exception/BusinessException.java
  exception/EntityNotFoundException.java
  util/DatabaseManager.java     — подключение к БД
  util/StringUtil.java
  util/ExcelExporter.java       — экспорт в .xlsx (Apache POI)
  util/CsvExporter.java         — экспорт в CSV
database/init.sql               — схема и тестовые данные
docker-compose.yml
exports/                        — сгенерированные файлы экспорта
```

## ER-модель (словесно)

- **buyers** — покупатели: `id` (PK), `full_name`, `email` (UNIQUE), `phone`, `address`.
- **orders** — заказы: `id` (PK), `buyer_id` (FK → buyers.id, ON DELETE CASCADE), `product_name`, `quantity`, `total_price`, `status`, `created_at`.
- Связь **1:N**: один покупатель — много заказов.

## Бизнес-правила (слой Service)

1. ФИО покупателя не может быть пустым.
2. Email покупателя уникален.
3. Заказ можно создать только для существующего покупателя (`buyer_id`).
4. Количество в заказе должно быть **> 0**.
5. Сумма заказа **≥ 0**.
6. Переходы статусов заказа:
   - `CREATED` → `PAID` | `CANCELLED`
   - `PAID` → `SHIPPED` | `CANCELLED`
   - `SHIPPED` → `DELIVERED`
   - `DELIVERED` → (дальнейшие изменения статуса запрещены)
   - `CANCELLED` → (дальнейшие изменения статуса запрещены)
7. Новый заказ создаётся только со статусом `CREATED`.

## Главное меню

1. Покупатели — CRUD  
2. Заказы — CRUD  
3. Поиск — по товару, по покупателю, покупатель по email  
4. Фильтрация и сортировка — статус, даты, сумма; сортировка по дате, сумме, названию  
5. Статистика — покупатели, заказы, суммы, EnumMap по статусам, отменённые  
6. Экспорт — Excel и CSV в каталог `exports/`  
7. Вывод таблиц buyers и orders  
0. Выход  

## Экспорт

Файлы сохраняются в каталог `exports/` в корне проекта (создаётся автоматически).
