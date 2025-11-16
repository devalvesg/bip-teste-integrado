FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy parent POM
COPY pom.xml .

# Copy backend module (dependency for EJB)
COPY backend-module ./backend-module

# Copy ejb module
COPY ejb-module ./ejb-module

# Build the project
RUN mvn clean install -DskipTests

# Final stage - EJB Module (Library only)
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/ejb-module/target/ejb-module-1.0.0.jar /app/ejb-app.jar

# This is a library module, not a runnable application
# It's used as a dependency by other modules
# This container is just for reference/verification

CMD ["echo", "EJB Module library - Use as dependency in other applications"]

