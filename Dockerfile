# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-jar", "target/sistema-0.0.1-SNAPSHOT.jar"]
