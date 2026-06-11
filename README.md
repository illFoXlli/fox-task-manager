# Fox Task Manager

Spring Boot application for personal notes with a Thymeleaf web interface, JWT cookie authentication, PostgreSQL, Telegram login flow, and REST API documentation via Swagger UI.

## Stack

- Java 21
- Spring Boot 3
- Spring MVC + Thymeleaf
- Spring Security
- JWT stored in HttpOnly cookies
- Spring Data JPA
- PostgreSQL
- Flyway migrations
- Swagger / OpenAPI via springdoc
- Gradle
- Docker Compose

## What The Application Does

The project has two interfaces over the same business logic:

- Web pages rendered by Thymeleaf: `/login`, `/register`, `/note/view`, `/note/list`, `/note/create`, `/note/edit`.
- REST API returning JSON: `/api/auth/**` and `/api/notes/**`.

Both interfaces use the same services, repositories, users, notes, and security rules.

## Run With Docker

```bash
docker compose down --remove-orphans
docker compose up -d --build
docker compose ps
```

Open:

```text
http://localhost:3000/login
```

Swagger UI:

```text
http://localhost:3000/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:3000/v3/api-docs
```

## Environment

Use `.env` for local secrets and deployment-specific values.

Important variables:

```env
APP_PORT=3000

DB_URL=jdbc:postgresql://host.docker.internal:5432/task_manager?options=-c%20TimeZone=UTC
DB_USER=...
DB_PASSWORD=...
FLYWAY_ENABLED=true

JWT_SECRET=...
JWT_ACCESS_EXPIRATION_MINUTES=
JWT_REFRESH_EXPIRATION_DAYS=30

ADMIN_LOGIN=admin
ADMIN_PASSWORD=...
ADMIN_DISPLAY_NAME=Admin

SPRING_SECURITY_USER_NAME=user
SPRING_SECURITY_USER_PASSWORD=default

APP_DEFAULT_USER_ENABLED=true
APP_DEFAULT_USER_LOGIN=user
APP_DEFAULT_USER_PASSWORD=jdbcDefault
APP_DEFAULT_USER_DISPLAY_NAME=user
APP_DEFAULT_USER_UPDATE_PASSWORD=true

COOKIE_SECURE=false
```

For HTTPS deployment set:

```env
COOKIE_SECURE=true
```

## Authentication

The app uses JWT tokens in HttpOnly cookies:

- `access_token` is short-lived.
- `refresh_token` is long-lived and stored in the database as a hash.
- `device_id` identifies the browser/device and helps avoid creating a new refresh-token row on every login.

Login flow:

```text
POST /api/auth/login
-> backend verifies credentials
-> backend sets HttpOnly cookies
-> browser automatically sends cookies on protected requests
```

Logout flow:

```text
POST /api/auth/logout
-> backend revokes current refresh token
-> backend clears auth cookies
```

## REST API

Auth endpoints:

```text
POST /api/auth/login
POST /api/auth/register
POST /api/auth/logout
```

Notes endpoints:

```text
GET    /api/notes
GET    /api/notes/{id}
POST   /api/notes
PUT    /api/notes/{id}
DELETE /api/notes/{id}
```

`/api/notes/**` requires authentication. First call `/api/auth/login`, then Swagger UI or the browser will send auth cookies automatically.

Example login request:

```json
{
  "login": "user",
  "password": "jdbcDefault"
}
```

Example create note request:

```json
{
  "title": "Plan",
  "content": "Finish REST API and Swagger"
}
```

## Swagger

Swagger UI is available at:

```text
http://localhost:3000/swagger-ui/index.html
```

How to test protected endpoints in Swagger:

1. Open Swagger UI.
2. Execute `POST /api/auth/login`.
3. After successful login, execute `/api/notes` endpoints.
4. Cookies are HttpOnly, so you will not paste tokens manually. The browser sends them automatically.

## Database Migrations

Flyway migrations are stored in:

```text
src/main/resources/db/migration
```

They create and evolve:

- `note`
- `user_profiles`
- `refresh_tokens`
- `telegram_auth_sessions`

The default homework user is not hardcoded in SQL. It is created by `DefaultUserInitializer` from ENV and the password is encoded with BCrypt at application startup.

## Tests And Quality Checks

Run:

```bash
./gradlew check
```

This runs:

- compilation
- tests
- Checkstyle
- Spotless format check

## Notes For Reviewers

The project contains both MVC pages and REST API.

`NoteController` is responsible for Thymeleaf pages.

`NoteRestController` is responsible for JSON REST API.

Both use `NoteService`, so note ownership and validation logic are shared.
