FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY common-api/pom.xml common-api/
COPY ejb-module/pom.xml ejb-module/
COPY backend-module/pom.xml backend-module/

RUN mvn dependency:go-offline -B || true

COPY common-api/src common-api/src
COPY ejb-module/src ejb-module/src
COPY backend-module/src backend-module/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/backend-module/target/backend-module-*-exec.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/swagger-ui.html || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]