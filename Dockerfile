# Build stage
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline -q
COPY src ./src
RUN mvn -B clean package -DskipTests -q

# Run stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/sample-app-2-0.0.1.jar /app/app.jar
COPY config.yml.template /app/config.yml
EXPOSE 8080 8081
CMD ["java", "-jar", "app.jar", "server", "config.yml"]
