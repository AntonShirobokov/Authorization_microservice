#1 Сборка проекта
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

#2. Запуск приложения
FROM eclipse-temurin:17-jdk
WORKDIR /authorization_microservice
COPY --from=builder /app/target/*.jar Authorization_microservice-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "Authorization_microservice-0.0.1-SNAPSHOT.jar"]