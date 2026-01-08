# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Копіюємо тільки pom.xml файли, щоб закешувати залежності (оптимізація)
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml

# Завантажуємо залежності (якщо не змінилися pom.xml, цей крок візьметься з кешу)
# Але для простоти можна скопіювати все одразу:
COPY . .

# Збираємо проєкт. -DskipTests пропускає тести, щоб пришвидшити збірку в хмарі
RUN mvn clean package -DskipTests -pl web -am

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копіюємо зібраний JAR з попереднього етапу
# Зверніть увагу: шлях до JAR залежить від версії в pom.xml.
# Використовуємо wildcard (*), щоб не залежати від версії.
COPY --from=build /app/web/target/web-*.jar app.jar

# Експонуємо порт (Render ігнорує це, але корисно для документації)
EXPOSE 8080

# Запуск
ENTRYPOINT ["java", "-jar", "app.jar"]