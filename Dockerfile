# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Install Node.js 20 (Debian-based)
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# 1. Cache Java dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline -DskipTests || true

# 2. Build Frontend
COPY frontend/package*.json ./frontend/
RUN cd frontend && npm install --legacy-peer-deps
COPY frontend/ ./frontend/
RUN cd frontend && npm run build

# 3. Copy Java source and build JAR
COPY src ./src
    
RUN mvn clean package -DskipTests -Dskip.frontend=true

# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Security: Run as non-root
RUN addgroup -S nss && adduser -S nss -G nss
USER nss

COPY --from=build /app/target/nss-platform-*.jar app.jar

ENV APP_PORT=8080
ENV PORT=8080
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]