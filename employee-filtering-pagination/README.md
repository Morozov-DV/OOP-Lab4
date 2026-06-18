# Варіант 4 — фільтрація та пагінація працівників

Навчальний Spring Boot REST API з використанням:

- Spring Web;
- Spring Data JPA;
- `JpaSpecificationExecutor`;
- H2;
- Spring Security;
- MockMvc та JUnit 5.

## Реалізований endpoint

```text
GET /api/employees
```

Параметри:

| Параметр | Значення |
|---|---|
| `position` | точна назва посади без урахування регістру |
| `minSalary` | мінімальна зарплата включно |
| `maxSalary` | максимальна зарплата включно |
| `page` | номер сторінки, починаючи з 0 |
| `size` | кількість записів, максимум 100 |
| `sortBy` | `id`, `firstName`, `lastName`, `position`, `baseSalary` |
| `direction` | `asc` або `desc` |

Облікові дані:

```text
admin / password
```

## Запуск без Docker

```bash
mvn clean test
mvn spring-boot:run
```

Після запуску:

```text
http://localhost:8080/api/employees
```

Приклад через curl:

```bash
curl -u admin:password \
  "http://localhost:8080/api/employees?position=Developer&minSalary=40000&maxSalary=60000&page=0&size=5"
```

## Запуск у GitHub Codespaces через Docker

```bash
docker compose up --build
```

Після цього відкрийте порт `8080` у вкладці **PORTS**.

## Модульні тести

Клас `EmployeeControllerIntegrationTest` містить п'ять тестів:

1. обмеження кількості записів пагінацією;
2. фільтрація за посадою;
3. комбінована фільтрація;
4. нормалізація некоректних параметрів пагінації;
5. заборона доступу неавторизованому користувачу.
