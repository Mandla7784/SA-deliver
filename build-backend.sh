#!/bin/bash

echo "🚀 Building SA-Deliver Java Backend for Railway..."

# Navigate to backend directory
cd backend

# Clean and package with Maven
echo "📦 Running Maven build..."
mvn clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Maven build completed successfully"
    echo "📁 JAR files created:"
    ls -la target/*.jar 2>/dev/null || echo "No JAR files found"
else
    echo "❌ Maven build failed"
    exit 1
fi

echo "🎉 SA-Deliver backend is ready for Railway deployment!"
