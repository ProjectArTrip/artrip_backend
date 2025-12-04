FROM --platform=linux/amd64 gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

COPY . /app

RUN gradle build -x test --no-daemon

FROM bellsoft/liberica-openjdk-alpine:17 AS develop

WORKDIR /app

RUN apk add --no-cache curl netcat-openbsd bash

ENV SPRING_PROFILES_ACTIVE=local

ENTRYPOINT ["sh", "-c", "./gradlew bootRun --no-daemon"]

FROM bellsoft/liberica-openjdk-alpine:17 AS production

WORKDIR /app

RUN apk add --no-cache curl netcat-openbsd

COPY --from=builder /app/build/libs/*.jar /app/artrip.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/artrip.jar"]
