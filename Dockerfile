# ── Stage 1: Build ────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Install Node.js for frontend build
RUN apk add --no-cache nodejs npm maven

# Dependency cache layer
COPY pom.xml ./
RUN mvn -B dependency:go-offline "-Dskip.frontend=true" -q 2>/dev/null || true

# Copy source
COPY src ./src
COPY frontend ./frontend

# Full build — Maven compiles Java + builds React via frontend-maven-plugin
RUN mvn -B package -DskipTests

# ── Stage 2: Runtime ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S nss && adduser -S nss -G nss
USER nss

COPY --from=build /app/target/nss-platform-*.jar app.jar

# Railway injects PORT automatically
ENV PORT=8080

EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
