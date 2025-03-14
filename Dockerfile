# Stage 1: Build ứng dụng Spring Boot
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests
#
## Stage 2: run application java
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]