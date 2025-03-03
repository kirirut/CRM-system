# CRM System

## Описание проекта
Этот проект представляет собой CRM-систему, разработанную с использованием Spring Boot и RESTful API. Система предназначена для управления клиентами, сделками и взаимодействиями с ними.

## Стек технологий
- **Java 17+**
- **Spring Boot** (Spring MVC, Spring Data JPA, Spring Security)
- **Maven** (для управления зависимостями)
- **PostgreSQL/MySQL** (или другая реляционная база данных)
- **Lombok** (для упрощения работы с моделями)
- **Swagger** (для документирования API)

## Установка и запуск

### 1. Клонирование репозитория
```bash
git clone <URL_РЕПОЗИТОРИЯ>
cd <ИМЯ_ПРОЕКТА>
```

### 2. Конфигурация базы данных
Перед запуском укажите параметры подключения в `application.properties` или `application.yml`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/crm_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Сборка и запуск

Собрать проект и запустить локально:
```bash
mvn clean install
mvn spring-boot:run
```

## API
После запуска CRM доступна через REST API. Документацию можно посмотреть в Swagger:
```
http://localhost:8080/swagger-ui/
```

## Основные эндпоинты
- `GET /api/customers` — получение списка клиентов
- `POST /api/customers` — добавление нового клиента
- `PUT /api/customers/{id}` — обновление данных клиента
- `DELETE /api/customers/{id}` — удаление клиента


