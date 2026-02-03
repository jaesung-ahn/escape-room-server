# Build stage is handled by Gradle before Docker build
# This Dockerfile expects the JAR to be pre-built

FROM eclipse-temurin:17-jre-jammy

LABEL org.opencontainers.image.source="https://github.com/wiiee/escape-room-server"
LABEL org.opencontainers.image.description="Wiiee Escape Room API Server"

# Create non-root user for security
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -s /bin/sh appuser

WORKDIR /app

# Copy the pre-built JAR
COPY api/build/libs/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

# JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 3781

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
