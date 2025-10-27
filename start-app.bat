@echo off
setlocal enabledelayedexpansion

REM SA-Deliver E-Commerce Application Startup Script for Windows
REM This script builds, starts the backend server, and opens the frontend in a browser

set "BACKEND_PORT=8080"
set "FRONTEND_PORT=3000"
set "PROJECT_ROOT=%~dp0"
set "BACKEND_DIR=%PROJECT_ROOT%backend"
set "FRONTEND_DIR=%PROJECT_ROOT%frontend"

echo [INFO] Starting SA-Deliver E-Commerce Application...
echo [INFO] Project root: %PROJECT_ROOT%

REM Check prerequisites
echo [INFO] Checking prerequisites...

where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo [ERROR] Please install Java 17 or higher
    pause
    exit /b 1
)

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo [ERROR] Please install Maven
    pause
    exit /b 1
)

echo [SUCCESS] Prerequisites check passed

REM Check if ports are available
netstat -an | findstr ":%BACKEND_PORT% " >nul
if %errorlevel% equ 0 (
    echo [WARNING] Port %BACKEND_PORT% is already in use. Attempting to kill existing process...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%BACKEND_PORT% "') do (
        taskkill /PID %%a /F >nul 2>&1
    )
    timeout /t 2 /nobreak >nul
)

REM Build the backend
echo [INFO] Building backend application...
cd /d "%BACKEND_DIR%"

mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Backend compilation failed
    pause
    exit /b 1
)

echo [SUCCESS] Backend compiled successfully

REM Start the backend server
echo [INFO] Starting backend server on port %BACKEND_PORT%...

start /b mvn exec:java -Dexec.mainClass="main.java.Server" -q

REM Wait for server to be ready
echo [INFO] Waiting for server to start...
timeout /t 10 /nobreak >nul

REM Test if server is ready
:test_server
curl -s "http://localhost:%BACKEND_PORT%/health" >nul 2>&1
if %errorlevel% neq 0 (
    echo Waiting for server...
    timeout /t 2 /nobreak >nul
    goto test_server
)

echo [SUCCESS] Backend server started successfully

REM Start a simple HTTP server for the frontend
echo [INFO] Starting frontend server on port %FRONTEND_PORT%...

cd /d "%FRONTEND_DIR%"

REM Try to start Python HTTP server
python -m http.server %FRONTEND_PORT% >nul 2>&1
if %errorlevel% neq 0 (
    python3 -m http.server %FRONTEND_PORT% >nul 2>&1
    if %errorlevel% neq 0 (
        echo [WARNING] No suitable HTTP server found. You can manually open the frontend/index.html file in your browser
    ) else (
        echo [SUCCESS] Frontend server started successfully
    )
) else (
    echo [SUCCESS] Frontend server started successfully
)

REM Open browser
echo [INFO] Opening application in browser...
start "http://localhost:%FRONTEND_PORT%"

REM Display application information
echo.
echo [SUCCESS] SA-Deliver E-Commerce Application is now running!
echo.
echo Application URLs:
echo   Frontend: http://localhost:%FRONTEND_PORT%
echo   Backend API: http://localhost:%BACKEND_PORT%
echo   Health Check: http://localhost:%BACKEND_PORT%/health
echo.
echo Test Credentials:
echo   Username: admin, Password: admin123
echo   Username: test, Password: test123
echo.
echo Available API Endpoints:
echo   GET  /api/products           - Get all products
echo   GET  /api/categories          - Get all categories
echo   POST /api/register            - Register new user
echo   POST /api/login               - User login
echo   GET  /api/profile             - Get user profile
echo.
echo Press any key to stop the application...
pause >nul

REM Cleanup
echo [INFO] Stopping servers...
taskkill /f /im java.exe >nul 2>&1
taskkill /f /im python.exe >nul 2>&1
taskkill /f /im python3.exe >nul 2>&1

echo [INFO] Application stopped.
pause
