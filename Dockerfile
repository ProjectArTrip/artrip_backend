FROM --platform=linux/amd64 gradle:8.5-jdk17-alpine AS builder

WORKDIR /app
COPY . .

RUN gradle build -x test --no-daemon

FROM bellsoft/liberica-openjdk-alpine:17 AS develop

WORKDIR /app

RUN apk add --no-cache curl netcat-openbsd

COPY --from=builder /app/build/libs/*.jar /app/artrip.jar

ENV SPRING_PROFILES_ACTIVE=local

ENTRYPOINT ["sh", "-c", "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar /app/artrip.jar"]

FROM develop as production

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/artrip.jar"]
