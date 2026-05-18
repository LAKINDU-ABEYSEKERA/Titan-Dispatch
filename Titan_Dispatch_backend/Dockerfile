# Stage 1: Build using official Maven image
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Production JRE Image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/Titan_Dispatch_backend-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080

# Enterprise JVM Tuning
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -Duser.timezone=UTC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]