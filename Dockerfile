FROM gradle:jdk21 AS builder

WORKDIR /app
COPY . .

RUN gradle :server:build --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/server/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]