# Руководство по развертыванию платформы ЦФА в Kubernetes

Данное руководство описывает процесс контейнеризации всех сервисов и их развертывание в кластере Kubernetes (Minikube, kind или облачный K8s).

## 1. Сборка Docker образов
Для работы в K8s необходимо сначала собрать образы. Если вы используете **Minikube**, сначала выполните:
```bash
eval $(minikube docker-env)
```

Затем соберите образы из корневой директории проекта:
```bash
docker build -t auth-service:latest -f auth-service/Dockerfile .
docker build -t api-gateway:latest -f api-gateway-service/Dockerfile .
docker build -t trading-engine:latest -f trading-engine-service/Dockerfile .
docker build -t solana-connector:latest -f solana-connector-service/Dockerfile .
```

---

## 2. Развертывание инфраструктуры
Инфраструктурные сервисы (БД, Kafka, Мониторинг) должны быть запущены в первую очередь.

```bash
# 1. Запуск PostgreSQL
kubectl apply -f k8s-manifests/infra/postgres.yaml

# 2. Запуск Kafka (Zookeeper + Kafka)
kubectl apply -f k8s-manifests/infra/kafka.yaml

# 3. Запуск Мониторинга (Prometheus + Grafana)
kubectl apply -f k8s-manifests/infra/monitoring.yaml
```

Убедитесь, что все поды запущены:
```bash
kubectl get pods
```

---

## 3. Развертывание микросервисов
После того как инфраструктура готова, запускаем бизнес-логику:

```bash
# 1. Сервис авторизации
kubectl apply -f k8s-manifests/auth-deployment.yaml

# 2. Ядро торгов
kubectl apply -f k8s-manifests/trading-engine-deployment.yaml

# 3. Блокчейн-коннектор
kubectl apply -f k8s-manifests/solana-connector-deployment.yaml

# 4. API Gateway (внешний вход)
kubectl apply -f k8s-manifests/api-gateway-deployment.yaml
```

---

## 4. Доступ к приложению
**API Gateway** опубликован через `NodePort` на порту **30080**.

Если вы используете **Minikube**, получите URL для доступа:
```bash
minikube service api-gateway --url
```

---

## 5. Мониторинг и логи
- **Просмотр логов любого сервиса:**
  ```bash
  kubectl logs -f deployment/trading-engine
  ```
- **Проброс портов для Grafana (порт 3000):**
  ```bash
  kubectl port-forward service/grafana 3000:3000
  ```
- **Проброс портов для Prometheus (порт 9090):**
  ```bash
  kubectl port-forward service/prometheus 9090:9090
  ```

---

## 6. Примечания для ВКР
- **Масштабируемость:** Для демонстрации масштабируемости (требование HFT) вы можете увеличить количество подов `trading-engine`:
  ```bash
  kubectl scale deployment/trading-engine --replicas=3
  ```
- **Отказоустойчивость:** Если вы удалите под с базой данных, Kubernetes автоматически перезапустит его, поддерживая работоспособность системы.
