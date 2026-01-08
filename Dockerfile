# --- Stage 1: Build ---
# Переконайтеся, що тут версія 21 (якщо ви оновили її для виправлення попередньої помилки)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml
COPY . .

# Збираємо проєкт
RUN mvn clean package -DskipTests -pl web -am

# --- Stage 2: Run ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# ВИПРАВЛЕННЯ ТУТ:
# 1. Ми точно знаємо шлях: /app/web/target/
# 2. Ми точно знаємо ім'я: web-app.jar (бо ми задали його в pom.xml)
COPY --from=build /app/web/target/web-app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]