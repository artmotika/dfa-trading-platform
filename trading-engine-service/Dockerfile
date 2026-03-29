FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
# Создаем директорию для отчетов (по требованию ЦФА)
RUN mkdir -p /reports && chown 1000:1000 /reports
USER 1000
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]