# Описание

Данное приложение представляет собой учебную систему обработки банковских транзакций, реализованную на базе Vert.x, PostgreSQL, jOOQ и XML (XSD/JAXB).

Сервис принимает XML-платежи, валидирует их по XSD-схеме, выполняет операцию пополнения или списания и вносит изменения в базу данных в рамках атомарной транзакции. При ошибках (некорректный XML, недостаточно средств, отсутствие банка и т.д.) транзакция автоматически откатывается.

Функциональность включает:

- REST API для получения информации о банках  
- XML API для выполнения платежей  
- валидацию входных данных  
- безопасную работу с PostgreSQL через jOOQ DSL  
- корректное управление транзакциями и обработку ошибок  

# Cборка и запуск

## 1. Поднять PostgreSQL и создать таблицы

```sql
CREATE DATABASE bankapp;
\c bankapp;

CREATE TABLE banks (
  bic VARCHAR PRIMARY KEY,
  name VARCHAR NOT NULL,
  balance NUMERIC NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  bic VARCHAR NOT NULL,
  amount NUMERIC NOT NULL,
  type VARCHAR NOT NULL,
  created_at TIMESTAMP NOT NULL
);
```

## 2. Генерация и сборка

```bash
mvn clean generate-sources
mvn package
```

## 3. Запуск

```bash
java -jar target/bankapp-1.0.0-fat.jar
```

# Пример входных данных

## Считать все банки
```
curl -v http://localhost:8080/api/banks
```

## Создать банк
```
curl -v -X POST http://localhost:8080/api/banks \
  -H "Content-Type: application/json" \
  -d '{"bic":"BANK999","name":"Another Bank","balance":50000.00}'
```

## Совершить перевод
 Создаем файл payment.xml
 ```xml
<?xml version="1.0" encoding="UTF-8"?>
<PaymentRequest xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:noNamespaceSchemaLocation="PaymentRequest.xsd">
    <SenderCard>1111222233334444</SenderCard>
    <ReceiverCard>5555666677778888</ReceiverCard>
    <SenderAccount>ACC-SENDER</SenderAccount>
    <ReceiverAccount>ACC-RECEIVER</ReceiverAccount>
    <Amount>1000.00</Amount>

    <AcquirerBic>BANK123</AcquirerBic>
    <IssuerBic>BANK123</IssuerBic>
    <ReceiverBic>BANK999</ReceiverBic>
</PaymentRequest>
```

и делаем запрос
```bash
curl -v -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/xml" \
  --data-binary @payment.xml
```
