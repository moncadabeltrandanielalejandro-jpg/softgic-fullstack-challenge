# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY backend/pom.xml backend/pom.xml
COPY backend/solicitudes-service/pom.xml backend/solicitudes-service/pom.xml
COPY backend/indicadores-service/pom.xml backend/indicadores-service/pom.xml
WORKDIR /workspace/backend
RUN mvn -q -pl indicadores-service -am dependency:go-offline
COPY backend/indicadores-service/src indicadores-service/src
RUN mvn -q -pl indicadores-service -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/backend/indicadores-service/target/indicadores-service.jar app.jar
USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
