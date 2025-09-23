FROM --platform=linux/amd64 gradle:8.5-jdk17-alpine AS builder

WORKDIR /app
COPY . .

RUN gradle build -x test

FROM bellsoft/liberica-openjdk-alpine:17

ENV SPRING_PROFILE=dev

COPY --from=builder /app/build/libs/*.jar /artrip.jar
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILE} -jar /artrip.jar"]
