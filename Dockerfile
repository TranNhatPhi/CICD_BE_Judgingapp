FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

## use jdk
FROM openjdk:17
WORKDIR /app

COPY target/judging-0.0.1-SNAPSHOT.jar /app/target/judging-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/app/target/judging-0.0.1-SNAPSHOT.jar"]
EXPOSE 9000