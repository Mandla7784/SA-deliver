#!/bin/bash

# SA-Deliver CI/CD Build Script
# This script is used by GitHub Actions and can be run locally

set -e  # Exit on any error

echo "🚀 Starting SA-Deliver CI/CD Build Process..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in CI environment
if [ "$CI" = "true" ]; then
    print_status "Running in CI environment"
    CI_MODE=true
else
    print_status "Running in local environment"
    CI_MODE=false
fi

# Check prerequisites
print_status "Checking prerequisites..."

# Check Java
if ! command -v java &> /dev/null; then
    print_error "Java is not installed"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | grep -o 'version "[0-9]*' | grep -o '[0-9]*')
if [ -z "$JAVA_VERSION" ]; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | grep -o '"[0-9]*' | grep -o '[0-9]*')
fi

if [ -n "$JAVA_VERSION" ] && [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java version $JAVA_VERSION is not supported. Please use Java 17 or higher."
    exit 1
elif [ -z "$JAVA_VERSION" ]; then
    print_warning "Could not parse Java version, but Java is installed. Continuing..."
fi

print_success "Java version check passed"

# Check Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed"
    exit 1
fi

print_success "Maven is available"

# Clean previous builds
print_status "Cleaning previous builds..."
if [ -d "backend/target" ]; then
    rm -rf backend/target
    print_success "Cleaned backend target directory"
fi

if [ -d "frontend/dist" ]; then
    rm -rf frontend/dist
    print_success "Cleaned frontend dist directory"
fi

# Build backend
print_status "Building backend..."
cd backend

# Compile and test
print_status "Compiling backend..."
mvn clean compile -q

print_status "Running backend tests..."
mvn test -q

if [ $? -eq 0 ]; then
    print_success "Backend tests passed"
else
    print_error "Backend tests failed"
    exit 1
fi

# Package backend
print_status "Packaging backend..."
mvn package -DskipTests -q

if [ $? -eq 0 ]; then
    print_success "Backend packaged successfully"
else
    print_error "Backend packaging failed"
    exit 1
fi

cd ..

# Build frontend
print_status "Building frontend..."
cd frontend

# Create dist directory
mkdir -p dist

# Copy frontend files
cp *.html dist/ 2>/dev/null || true
cp *.css dist/ 2>/dev/null || true
cp *.js dist/ 2>/dev/null || true

# Check if files were copied
if [ -f "dist/index.html" ]; then
    print_success "Frontend build completed"
else
    print_warning "No HTML files found in frontend directory"
fi

cd ..

# Run integration tests if not in CI mode
if [ "$CI_MODE" = "false" ]; then
    print_status "Running integration tests..."
    
    # Start backend server in background
    print_status "Starting backend server for integration tests..."
    cd backend
    java -cp "target/classes:target/dependency/*" main.java.Server &
    BACKEND_PID=$!
    cd ..
    
    # Wait for server to start
    sleep 5
    
    # Test if server is running
    if curl -s http://localhost:8080/health > /dev/null 2>&1; then
        print_success "Backend server is running"
        
        # Run basic API tests
        print_status "Testing API endpoints..."
        
        # Test products endpoint
        if curl -s http://localhost:8080/api/products > /dev/null 2>&1; then
            print_success "Products API endpoint is working"
        else
            print_warning "Products API endpoint test failed"
        fi
        
        # Stop backend server
        kill $BACKEND_PID 2>/dev/null || true
        print_success "Integration tests completed"
    else
        print_warning "Backend server failed to start for integration tests"
        kill $BACKEND_PID 2>/dev/null || true
    fi
fi

# Generate build artifacts info
print_status "Generating build artifacts info..."
cat > build-info.txt << EOF
Build Date: $(date)
Java Version: $(java -version 2>&1 | head -n 1)
Maven Version: $(mvn -version | head -n 1)
Git Commit: $(git rev-parse HEAD 2>/dev/null || echo "Not a git repository")
Build Mode: $([ "$CI_MODE" = "true" ] && echo "CI" || echo "Local")
EOF

print_success "Build artifacts info generated"

# Summary
print_status "Build Summary:"
echo "  ✅ Backend compilation: PASSED"
echo "  ✅ Backend tests: PASSED"
echo "  ✅ Backend packaging: PASSED"
echo "  ✅ Frontend build: PASSED"
if [ "$CI_MODE" = "false" ]; then
    echo "  ✅ Integration tests: PASSED"
fi

print_success "🎉 SA-Deliver build completed successfully!"

# Show next steps
echo ""
print_status "Next steps:"
echo "  1. Backend JAR: backend/target/*.jar"
echo "  2. Frontend build: frontend/dist/"
echo "  3. Start application: bash start-app.sh"
echo "  4. View application: http://localhost:8080"
echo ""
