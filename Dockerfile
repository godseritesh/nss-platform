# ── Stage 1: Build ────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Install build tools
RUN apk add --no-cache nodejs npm maven

# 1. Copy POM and go offline for Java deps
COPY pom.xml ./
RUN mvn -B dependency:go-offline "-Dskip.frontend=true"

# 2. Copy Frontend package files and install for caching
COPY frontend/package*.json ./frontend/
RUN cd frontend && npm install

# 3. Copy everything else and build
COPY . .
RUN mvn -B package -DskipTests -Dfrontend-maven-plugin.installNodeAndNpm.skip=true

# ── Stage 2: Runtime ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S nss && adduser -S nss -G nss
USER nss

COPY --from=build /app/target/nss-platform-*.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
