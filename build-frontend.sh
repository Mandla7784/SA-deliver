#!/bin/bash

# SA-Deliver Frontend Build Script for Netlify
echo "🚀 Building SA-Deliver Frontend for Netlify..."

# Create dist directory
mkdir -p dist

# Copy frontend files
cp frontend/*.html dist/ 2>/dev/null || echo "No HTML files found"
cp frontend/*.css dist/ 2>/dev/null || echo "No CSS files found"
cp frontend/*.js dist/ 2>/dev/null || echo "No JS files found"

# Check if files were copied
if [ -f "dist/index.html" ]; then
    echo "✅ Frontend build completed successfully"
    echo "📁 Files in dist/:"
    ls -la dist/
else
    echo "❌ Frontend build failed - no index.html found"
    exit 1
fi

echo "🎉 SA-Deliver frontend is ready for Netlify deployment!"
