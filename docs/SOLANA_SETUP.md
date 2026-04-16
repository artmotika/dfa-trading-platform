# Руководство по развертыванию смарт-контракта в Solana Devnet

В этом руководстве описано, как подготовить инструменты Solana, создать кошелек и развернуть программу (смарт-контракт) из папки `smart-contract`.

---

## 1. Установка инструментов (CLI)

Для работы с Solana вам понадобятся:
1.  **Rust:** `curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh`
2.  **Solana CLI:** 
    ```bash
    sh -c "$(curl -sSfL https://release.solana.com/v1.18.4/install)"
    ```
3.  **Anchor Framework** (используется в проекте):
    ```bash
    cargo install --git https://github.com/coral-xyz/anchor avm --locked --force
    avm install latest
    avm use latest
    ```

---

## 2. Настройка кошелька и сети

### Переключение на Devnet
Solana CLI должна знать, с какой сетью мы работаем:
```bash
solana config set --url https://api.devnet.solana.com
```

### Создание кошелька администратора
Этот кошелек будет владельцем платформы и оплачивать транзакции деплоя:
```bash
solana-keygen new --outfile ~/.config/solana/id.json
```
*Запишите seed-фразу в безопасное место.*

### Получение тестовых SOL (Airdrop)
Для деплоя нужны монеты (в Devnet они бесплатные):
```bash
solana airdrop 2
```
Проверьте баланс: `solana balance`

---

## 3. Подготовка и сборка контракта

Перейдите в папку со смарт-контрактом:
```bash
cd smart-contract
```

### Инициализация проекта Anchor (если папка пуста)
Если у вас только `lib.rs`, создайте структуру проекта:
```bash
anchor init dfa_advanced_platform
# Скопируйте содержимое вашего lib.rs в dfa_advanced_platform/programs/dfa_advanced_platform/src/lib.rs
```

### Сборка программы
```bash
anchor build
```
После сборки вы получите адрес программы (Program ID). Узнать его можно командой:
```bash
solana address -k target/deploy/dfa_advanced_platform-keypair.json
```

---

## 4. Развертывание (Deploy)

1.  **Обновите Program ID:**
    Откройте `lib.rs` и замените значение в `declare_id!("...")` на тот адрес, который вы получили шагом выше.
    Также обновите его в `Anchor.toml`.

2.  **Деплой:**
    ```bash
    anchor deploy
    ```

После успешного выполнения вы увидите сообщение `Deploy success`. Теперь ваша программа живет в блокчейне Solana Devnet!

---

## 5. Интеграция с Java-сервисами

Чтобы микросервисы могли взаимодействовать с контрактом:
1.  Скопируйте полученный **Program ID** в `SolanaBlockchainService.java` (переменная `programId`).
2.  Убедитесь, что в `application.yaml` сервиса `solana-connector` указан URL Devnet: `https://api.devnet.solana.com`.

---

## 6. Локальная разработка (Local Validator)
Если у вас нет интернета или Devnet работает медленно, вы можете запустить **собственный клон блокчейна** локально:
```bash
solana-test-validator
```
В другом терминале переключите конфиг на localhost:
```bash
solana config set --url http://127.0.0.1:8899
```
И деплойте туда так же через `anchor deploy`.
