# SA-Deliver Docker Configuration

# Multi-stage build for production deployment
FROM openjdk:21-jdk-slim AS backend-builder

# Set working directory
WORKDIR /app

# Copy Maven files
COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Build backend
WORKDIR /app/backend
RUN mvn clean package -DskipTests

# Frontend build stage
FROM node:18-alpine AS frontend-builder

WORKDIR /app
COPY frontend/ frontend/

# Build frontend (if package.json exists)
WORKDIR /app/frontend
RUN if [ -f package.json ]; then npm ci && npm run build; else echo "No package.json found, copying static files"; fi

# Production stage
FROM openjdk:21-jre-slim

# Install nginx for serving frontend
RUN apt-get update && apt-get install -y nginx && rm -rf /var/lib/apt/lists/*

# Copy backend JAR
COPY --from=backend-builder /app/backend/target/*.jar /app/app.jar

# Copy frontend build
COPY --from=frontend-builder /app/frontend/dist/ /var/www/html/

# Copy nginx configuration
COPY docker/nginx.conf /etc/nginx/nginx.conf

# Expose ports
EXPOSE 80 8080

# Create startup script
RUN echo '#!/bin/bash\n\
# Start nginx\n\
nginx\n\
# Start backend\n\
java -jar /app/app.jar &\n\
# Wait for processes\n\
wait' > /app/start.sh && chmod +x /app/start.sh

# Set working directory
WORKDIR /app

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Start application
CMD ["/app/start.sh"]