# Этап сборки
FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /app

# Копируем только необходимые файлы для загрузки зависимостей
COPY mvnw .
COPY .mvn/ .mvn
COPY pom.xml .

# Даем права на выполнение mvnw
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw package -DskipTests

# Этап запуска
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]