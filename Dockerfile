# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Копіюємо все
COPY . .

# Запускаємо збірку
RUN mvn clean package -DskipTests -pl web -am

# --- ДІАГНОСТИКА (ВИВЕСТИ ФАЙЛИ В ЛОГ) ---
# Цей рядок покаже в логах Render, що саме створив Maven
RUN echo "======= ВМІСТ ПАПКИ web/target =======" && \
    ls -la web/target/ && \
    echo "========================================"

# --- МАГІЯ ПОШУКУ ---
# Знаходимо будь-який JAR (окрім original) і копіюємо його в корінь як app.jar
# Якщо файлу немає, ця команда впаде з помилкою, і ми це побачимо
RUN find web/target -name "*.jar" ! -name "*original*" -exec cp {} /app/app.jar \;

# --- Stage 2: Run ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копіюємо файл, який ми підготували на попередньому етапі
COPY --from=build /app/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]