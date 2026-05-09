# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Install Node.js 20
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
# Ensure static dir exists and copy built assets
RUN mkdir -p src/main/resources/static && \
    cp -r frontend/dist/* src/main/resources/static/
    
RUN mvn clean package -DskipTests

# ---------- RUN STAGE ----------
FROM eclipse-temurin:21-jre-alpine

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