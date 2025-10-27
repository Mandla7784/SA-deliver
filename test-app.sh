#!/bin/bash

# Simple test script for SA-Deliver application

echo "Testing SA-Deliver Application..."

# Test if server is running
if curl -s "http://localhost:8080/health" >/dev/null 2>&1; then
    echo "✓ Backend server is running"
    
    # Test API endpoints
    echo "Testing API endpoints..."
    
    # Test products endpoint
    if curl -s "http://localhost:8080/api/products" >/dev/null 2>&1; then
        echo "✓ Products endpoint working"
    else
        echo "✗ Products endpoint failed"
    fi
    
    # Test categories endpoint
    if curl -s "http://localhost:8080/api/categories" >/dev/null 2>&1; then
        echo "✓ Categories endpoint working"
    else
        echo "✗ Categories endpoint failed"
    fi
    
    # Test registration
    REGISTER_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/register" \
        -H "Content-Type: application/json" \
        -d '{"username":"testuser","password":"testpass","email":"test@example.com"}')
    
    if echo "$REGISTER_RESPONSE" | grep -q "success"; then
        echo "✓ Registration endpoint working"
    else
        echo "✗ Registration endpoint failed"
    fi
    
    echo ""
    echo "Application test completed!"
    echo "Frontend should be available at: http://localhost:3000"
    
else
    echo "✗ Backend server is not running"
    echo "Please start the server first using one of the startup scripts:"
    echo "  - start-app.sh (Linux/macOS)"
    echo "  - start-app.bat (Windows)"
    echo "  - start-app.ps1 (PowerShell)"
fi
