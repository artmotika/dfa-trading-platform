# Инструкция по запуску платформы ЦФА

## Требования
*   **Java 21** (JDK 21+)
*   **Docker & Docker Compose**
*   **Gradle 8.x+** (или используйте встроенный `gradlew`)

---

## Шаг 1: Запуск инфраструктуры
Запустите базу данных, брокер сообщений и систему мониторинга:
```bash
docker-compose up -d
```
Это поднимет:
- **PostgreSQL** (порт 54321) — база данных.
- **Kafka & Zookeeper** (порт 9092) — шина событий.
- **Prometheus** (порт 9090) — сбор метрик.
- **Grafana** (порт 3000) — визуализация (логин: `admin`, пароль: `admin`).

---

## Шаг 2: Сборка и запуск микросервисов
Откройте 4 терминала и запустите сервисы по порядку (либо используйте IDE):

1. **Auth Service (Порт 8083):**
   ```bash
   ./gradlew :auth-service:bootRun
   ```
2. **Trading Engine (Порт 8081):**
   ```bash
   ./gradlew :trading-engine-service:bootRun
   ```
3. **Solana Connector (Порт 8082):**
   ```bash
   ./gradlew :solana-connector-service:bootRun
   ```
4. **API Gateway (Порт 8080):**
   ```bash
   ./gradlew :api-gateway-service:bootRun
   ```

---

## Шаг 3: Доступ к интерфейсам
- **API Документация (Swagger):** `http://localhost:8080/swagger-ui.html`
- **Панель Администратора:** Откройте файл `admin-ui/index.html` в любом браузере.
- **Метрики (Prometheus):** `http://localhost:9090`
- **Дашборды (Grafana):** `http://localhost:3000`

---

## Настройка Solana (Devnet)
Сервис настроен на работу с **Solana Devnet**. 
- Убедитесь, что у вас есть интернет-соединение.
- В `solana-connector-service` используется публичный RPC узел `https://api.devnet.solana.com`.
- Для реальных транзакций в `SolanaBlockchainService` необходимо прописать приватный ключ в переменную `adminAccount` (сейчас генерируется новый для демонстрации потока данных).
