# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY backend/pom.xml backend/pom.xml
COPY backend/solicitudes-service/pom.xml backend/solicitudes-service/pom.xml
COPY backend/indicadores-service/pom.xml backend/indicadores-service/pom.xml
WORKDIR /workspace/backend
COPY backend/solicitudes-service/src solicitudes-service/src
RUN mvn -q -pl solicitudes-service -am package -DskipTests

FROM eclipse-temurin:25-jre-alpine AS runtime
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/backend/solicitudes-service/target/solicitudes-service.jar app.jar
USER app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
