FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app
COPY . .
RUN ./gradlew :solana-connector-service:build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/solana-connector-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
