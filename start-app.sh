#!/bin/bash

# SA-Deliver E-Commerce Application Startup Script
# This script builds, starts the backend server, and opens the frontend in a browser

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BACKEND_PORT=8080
FRONTEND_PORT=3000
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend"
FRONTEND_DIR="$PROJECT_ROOT/frontend"

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

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if a port is in use
port_in_use() {
    lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1
}

# Function to wait for server to be ready
wait_for_server() {
    local port=$1
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for server to start on port $port..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "http://localhost:$port/health" >/dev/null 2>&1; then
            print_success "Server is ready!"
            return 0
        fi
        
        echo -n "."
        sleep 1
        attempt=$((attempt + 1))
    done
    
    print_error "Server failed to start within $max_attempts seconds"
    return 1
}

# Function to cleanup on exit
cleanup() {
    print_status "Cleaning up..."
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null || true
    fi
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null || true
    fi
}

# Set up signal handlers
trap cleanup EXIT INT TERM

# Main execution
main() {
    print_status "Starting SA-Deliver E-Commerce Application..."
    print_status "Project root: $PROJECT_ROOT"
    
    # Check prerequisites
    print_status "Checking prerequisites..."
    
    if ! command_exists java; then
        print_error "Java is not installed or not in PATH"
        print_error "Please install Java 17 or higher"
        exit 1
    fi
    
    if ! command_exists mvn; then
        print_error "Maven is not installed or not in PATH"
        print_error "Please install Maven"
        exit 1
    fi
    
    # Check Java version
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
    
    print_success "Prerequisites check passed"
    
    # Check if ports are available
    if port_in_use $BACKEND_PORT; then
        print_warning "Port $BACKEND_PORT is already in use. Attempting to kill existing process..."
        lsof -ti:$BACKEND_PORT | xargs kill -9 2>/dev/null || true
        sleep 2
    fi
    
    # Build the backend
    print_status "Building backend application..."
    cd "$BACKEND_DIR"
    
    if ! mvn clean compile -q; then
        print_error "Backend compilation failed"
        exit 1
    fi
    
    print_success "Backend compiled successfully"
    
    # Start the backend server
    print_status "Starting backend server on port $BACKEND_PORT..."
    
    # Start server in background
    mvn exec:java -Dexec.mainClass="main.java.Server" -q &
    BACKEND_PID=$!
    
    # Wait for server to be ready
    if ! wait_for_server $BACKEND_PORT; then
        print_error "Backend server failed to start"
        exit 1
    fi
    
    print_success "Backend server started successfully (PID: $BACKEND_PID)"
    
    # Start a simple HTTP server for the frontend
    print_status "Starting frontend server on port $FRONTEND_PORT..."
    
    cd "$FRONTEND_DIR"
    
    # Check if Python is available for simple HTTP server
    if command_exists python3; then
        python3 -m http.server $FRONTEND_PORT >/dev/null 2>&1 &
        FRONTEND_PID=$!
    elif command_exists python; then
        python -m SimpleHTTPServer $FRONTEND_PORT >/dev/null 2>&1 &
        FRONTEND_PID=$!
    elif command_exists node; then
        npx http-server -p $FRONTEND_PORT >/dev/null 2>&1 &
        FRONTEND_PID=$!
    else
        print_warning "No suitable HTTP server found. You can manually open the frontend/index.html file in your browser"
        FRONTEND_PID=""
    fi
    
    if [ ! -z "$FRONTEND_PID" ]; then
        sleep 2
        print_success "Frontend server started successfully (PID: $FRONTEND_PID)"
    fi
    
    # Open browser
    print_status "Opening application in browser..."
    
    if command_exists open; then
        # macOS
        open "http://localhost:$FRONTEND_PORT"
    elif command_exists xdg-open; then
        # Linux
        xdg-open "http://localhost:$FRONTEND_PORT"
    elif command_exists start; then
        # Windows (Git Bash)
        start "http://localhost:$FRONTEND_PORT"
    else
        print_warning "Could not automatically open browser. Please manually navigate to:"
        print_warning "Frontend: http://localhost:$FRONTEND_PORT"
        print_warning "Backend API: http://localhost:$BACKEND_PORT"
    fi
    
    # Display application information
    echo ""
    print_success "SA-Deliver E-Commerce Application is now running!"
    echo ""
    echo -e "${GREEN}Application URLs:${NC}"
    echo -e "  Frontend: ${BLUE}http://localhost:$FRONTEND_PORT${NC}"
    echo -e "  Backend API: ${BLUE}http://localhost:$BACKEND_PORT${NC}"
    echo -e "  Health Check: ${BLUE}http://localhost:$BACKEND_PORT/health${NC}"
    echo ""
    echo -e "${GREEN}Test Credentials:${NC}"
    echo -e "  Username: ${YELLOW}admin${NC}, Password: ${YELLOW}admin123${NC}"
    echo -e "  Username: ${YELLOW}test${NC}, Password: ${YELLOW}test123${NC}"
    echo ""
    echo -e "${GREEN}Available API Endpoints:${NC}"
    echo -e "  GET  ${BLUE}/api/products${NC}           - Get all products"
    echo -e "  GET  ${BLUE}/api/categories${NC}          - Get all categories"
    echo -e "  POST ${BLUE}/api/register${NC}            - Register new user"
    echo -e "  POST ${BLUE}/api/login${NC}               - User login"
    echo -e "  GET  ${BLUE}/api/profile${NC}             - Get user profile"
    echo ""
    print_status "Press Ctrl+C to stop the application"
    
    # Keep the script running
    while true; do
        sleep 1
        
        # Check if backend is still running
        if ! kill -0 $BACKEND_PID 2>/dev/null; then
            print_error "Backend server stopped unexpectedly"
            break
        fi
        
        # Check if frontend is still running 
        if [ ! -z "$FRONTEND_PID" ] && ! kill -0 $FRONTEND_PID 2>/dev/null; then
            print_warning "Frontend server stopped unexpectedly"
        fi
    done
}

# Run main function
main "$@"
