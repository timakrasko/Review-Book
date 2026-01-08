# --- Stage 1: Build ---
# ЗМІНЕНО: 17 -> 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml
COPY . .
RUN mvn clean package -DskipTests -pl web -am

# --- Stage 2: Run ---
# ЗМІНЕНО: 17 -> 21 (alpine версія може бути недоступна для 21, тому беремо звичайну або перевіряємо тег)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/web/target/web-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]