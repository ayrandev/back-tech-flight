# Etapa 1: build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY flightontime/pom.xml .
COPY flightontime/.mvn .mvn
COPY flightontime/mvnw .
COPY flightontime/mvnw.cmd .
RUN ./mvnw -B dependency:go-offline

COPY flightontime/src src
RUN ./mvnw -B clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
