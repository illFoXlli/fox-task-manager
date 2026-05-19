# Fox Task Manager

Task management backend service built with Spring Boot.

## Tech Stack

- Java 17
- Spring Boot
- Gradle
- Checkstyle
- Spotless

## Features

- Note CRUD service
- In-memory note storage
- Custom exception handling
- Git hooks
- Checkstyle validation
- Spotless formatting

## Project Structure

src/main/java/com/fox/taskmanager

- config
- controller
- dto
- exception
- model
- repository
- service
- storage

## Run Project

```bash
./gradlew bootRun
```

## Run Checks

```bash
./gradlew check
```

## Run Formatter

```bash
./gradlew spotlessApply
```

## Current Functionality

- Create note
- Update note
- Delete note
- Get note by id
- Get all notes

## Environment Variables

Create `.env` file in project root:

```env
APP_PORT=3999
```

## Future Plans

- PostgreSQL
- Docker Compose
- Flyway migrations
- REST API
- Swagger
- Authentication
- Telegram bot integration