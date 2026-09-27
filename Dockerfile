FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src/main/java/com/sental/bank ./src/main/java/com/sental/bank
COPY src/main/resources/ ./src/main/resources/
RUN mvn -q -DskipTests package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/kafka-bank-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
