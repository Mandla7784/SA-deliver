# SA-Deliver E-Commerce Application Startup Script for PowerShell
# This script builds, starts the backend server, and opens the frontend in a browser

param(
    [switch]$SkipBrowser
)

$ErrorActionPreference = "Stop"

# Configuration
$BACKEND_PORT = 8080
$FRONTEND_PORT = 3000
$PROJECT_ROOT = Split-Path -Parent $MyInvocation.MyCommand.Path
$BACKEND_DIR = Join-Path $PROJECT_ROOT "backend"
$FRONTEND_DIR = Join-Path $PROJECT_ROOT "frontend"

# Colors for output
$Colors = @{
    Red = "Red"
    Green = "Green"
    Yellow = "Yellow"
    Blue = "Blue"
    White = "White"
}

function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Colors.Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor $Colors.Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $Colors.Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $Colors.Red
}

function Test-Command {
    param([string]$Command)
    try {
        Get-Command $Command -ErrorAction Stop | Out-Null
        return $true
    }
    catch {
        return $false
    }
}

function Test-Port {
    param([int]$Port)
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect("localhost", $Port)
        $connection.Close()
        return $true
    }
    catch {
        return $false
    }
}

function Wait-ForServer {
    param([int]$Port, [int]$MaxAttempts = 30)
    
    Write-Status "Waiting for server to start on port $Port..."
    
    for ($i = 1; $i -le $MaxAttempts; $i++) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port/health" -TimeoutSec 1 -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                Write-Success "Server is ready!"
                return $true
            }
        }
        catch {
            # Server not ready yet
        }
        
        Write-Host "." -NoNewline
        Start-Sleep -Seconds 1
    }
    
    Write-Host ""
    Write-Error "Server failed to start within $MaxAttempts seconds"
    return $false
}

# Main execution
try {
    Write-Status "Starting SA-Deliver E-Commerce Application..."
    Write-Status "Project root: $PROJECT_ROOT"
    
    # Check prerequisites
    Write-Status "Checking prerequisites..."
    
    if (-not (Test-Command "java")) {
        Write-Error "Java is not installed or not in PATH"
        Write-Error "Please install Java 17 or higher"
        exit 1
    }
    
    if (-not (Test-Command "mvn")) {
        Write-Error "Maven is not installed or not in PATH"
        Write-Error "Please install Maven"
        exit 1
    }
    
    # Check Java version
    $javaVersion = (java -version 2>&1 | Select-String "version" | ForEach-Object { $_.Line.Split('"')[1] }).Split('.')[0]
    if ([int]$javaVersion -lt 17) {
        Write-Error "Java version $javaVersion is not supported. Please use Java 17 or higher."
        exit 1
    }
    
    Write-Success "Prerequisites check passed"
    
    # Check if ports are available
    if (Test-Port $BACKEND_PORT) {
        Write-Warning "Port $BACKEND_PORT is already in use. Attempting to kill existing process..."
        Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
    }
    
    # Build the backend
    Write-Status "Building backend application..."
    Set-Location $BACKEND_DIR
    
    $buildResult = & mvn clean compile -q
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Backend compilation failed"
        exit 1
    }
    
    Write-Success "Backend compiled successfully"
    
    # Start the backend server
    Write-Status "Starting backend server on port $BACKEND_PORT..."
    
    # Start server in background
    $backendJob = Start-Job -ScriptBlock {
        param($BackendDir)
        Set-Location $BackendDir
        mvn exec:java -Dexec.mainClass="main.java.Server" -q
    } -ArgumentList $BACKEND_DIR
    
    # Wait for server to be ready
    if (-not (Wait-ForServer $BACKEND_PORT)) {
        Write-Error "Backend server failed to start"
        Stop-Job $backendJob
        Remove-Job $backendJob
        exit 1
    }
    
    Write-Success "Backend server started successfully"
    
    # Start a simple HTTP server for the frontend
    Write-Status "Starting frontend server on port $FRONTEND_PORT..."
    
    Set-Location $FRONTEND_DIR
    
    $frontendJob = $null
    if (Test-Command "python") {
        $frontendJob = Start-Job -ScriptBlock {
            param($FrontendDir, $Port)
            Set-Location $FrontendDir
            python -m http.server $Port
        } -ArgumentList $FRONTEND_DIR, $FRONTEND_PORT
    }
    elseif (Test-Command "python3") {
        $frontendJob = Start-Job -ScriptBlock {
            param($FrontendDir, $Port)
            Set-Location $FrontendDir
            python3 -m http.server $Port
        } -ArgumentList $FRONTEND_DIR, $FRONTEND_PORT
    }
    else {
        Write-Warning "No suitable HTTP server found. You can manually open the frontend/index.html file in your browser"
    }
    
    if ($frontendJob) {
        Start-Sleep -Seconds 2
        Write-Success "Frontend server started successfully"
    }
    
    # Open browser
    if (-not $SkipBrowser) {
        Write-Status "Opening application in browser..."
        Start-Process "http://localhost:$FRONTEND_PORT"
    }
    
    # Display application information
    Write-Host ""
    Write-Success "SA-Deliver E-Commerce Application is now running!"
    Write-Host ""
    Write-Host "Application URLs:" -ForegroundColor $Colors.Green
    Write-Host "  Frontend: http://localhost:$FRONTEND_PORT" -ForegroundColor $Colors.Blue
    Write-Host "  Backend API: http://localhost:$BACKEND_PORT" -ForegroundColor $Colors.Blue
    Write-Host "  Health Check: http://localhost:$BACKEND_PORT/health" -ForegroundColor $Colors.Blue
    Write-Host ""
    Write-Host "Test Credentials:" -ForegroundColor $Colors.Green
    Write-Host "  Username: admin, Password: admin123" -ForegroundColor $Colors.Yellow
    Write-Host "  Username: test, Password: test123" -ForegroundColor $Colors.Yellow
    Write-Host ""
    Write-Host "Available API Endpoints:" -ForegroundColor $Colors.Green
    Write-Host "  GET  /api/products           - Get all products" -ForegroundColor $Colors.Blue
    Write-Host "  GET  /api/categories          - Get all categories" -ForegroundColor $Colors.Blue
    Write-Host "  POST /api/register            - Register new user" -ForegroundColor $Colors.Blue
    Write-Host "  POST /api/login               - User login" -ForegroundColor $Colors.Blue
    Write-Host "  GET  /api/profile             - Get user profile" -ForegroundColor $Colors.Blue
    Write-Host ""
    Write-Status "Press Ctrl+C to stop the application"
    
    # Keep the script running
    try {
        while ($true) {
            Start-Sleep -Seconds 1
            
            # Check if backend is still running
            if ($backendJob.State -eq "Failed" -or $backendJob.State -eq "Completed") {
                Write-Error "Backend server stopped unexpectedly"
                break
            }
            
            # Check if frontend is still running (if it was started)
            if ($frontendJob -and ($frontendJob.State -eq "Failed" -or $frontendJob.State -eq "Completed")) {
                Write-Warning "Frontend server stopped unexpectedly"
            }
        }
    }
    finally {
        # Cleanup
        Write-Status "Cleaning up..."
        if ($backendJob) {
            Stop-Job $backendJob -ErrorAction SilentlyContinue
            Remove-Job $backendJob -ErrorAction SilentlyContinue
        }
        if ($frontendJob) {
            Stop-Job $frontendJob -ErrorAction SilentlyContinue
            Remove-Job $frontendJob -ErrorAction SilentlyContinue
        }
    }
}
catch {
    Write-Error "An error occurred: $($_.Exception.Message)"
    exit 1
}
