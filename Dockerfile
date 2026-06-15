# ============================================
# Stage 1: Build the JAR with Maven
# ============================================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only pom.xml first — Docker caches this layer.
# Dependencies only re-download if pom.xml changes,
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================
# Stage 2: Minimal runtime image
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy ONLY the built jar from the build stage —
# Maven, source code, and the entire build toolchain are discarded
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Container-aware memory limits — important when running
# in resource-constrained environments (free-tier hosting, etc.)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]