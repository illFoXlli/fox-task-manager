# Fox Task Manager

Task management backend service built with Spring Boot.

## Tech Stack

- Java 21
- Spring Boot
- Gradle
- Checkstyle
- Spotless

## Features

- Note CRUD service
- In-memory note storage
- Custom exception handling
- Checkstyle validation
- Spotless formatting

## Project Structure

src/main/java/com/fox/taskmanager

- exception
- model
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

## Future Plans

- PostgreSQL
- Docker Compose
- Flyway migrations
- REST API
- Swagger
- Authentication
- Telegram bot integration
