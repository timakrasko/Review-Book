# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml
COPY . .

# 1. Збираємо проєкт
RUN mvn clean package -DskipTests -pl web -am

# 2. УНІВЕРСАЛЬНИЙ ФІКС:
# Знаходимо згенерований .jar (ігноруючи .original файл, який створює Spring)
# і перейменовуємо його на app.jar прямо тут.
RUN find web/target -maxdepth 1 -name "*.jar" ! -name "*original*" -exec mv {} web/target/app.jar \;

# --- Stage 2: Run ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Тепер ми точно знаємо, що файл називається app.jar, бо ми його перейменували вище
COPY --from=build /app/web/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]