FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app
COPY . .
RUN ./gradlew :trading-engine-service:build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/trading-engine-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
