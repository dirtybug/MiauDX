# MiauDX - Run tests and builds in Docker Container (PowerShell)
param (
    [string]$Target = "all"
)

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

# Initialize and verify Yaesu FT-891 Radio Simulator
if (Get-Command python -ErrorAction SilentlyContinue) {
    if (Test-Path ".\tools\ft891_simulator.py") {
        Write-Host "[RADIO-SIM] Starting Yaesu FT-891 Radio Simulator verification..." -ForegroundColor Cyan
        python .\tools\ft891_simulator.py --test
        Write-Host "[RADIO-SIM] Yaesu FT-891 Radio Simulator verified and ready!" -ForegroundColor Green
        Write-Host ""
    }
}

# Check if Docker exists
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Docker not found in PATH. Please install Docker Desktop." -ForegroundColor Red
    exit 1
}

# Check if Docker daemon is running
docker info >$null 2>&1
if ($LASTEXITCODE -ne 0) {
    if (Get-Command wsl -ErrorAction SilentlyContinue) {
        Write-Host "[INFO] Windows Docker Desktop not active. Running via WSL Docker engine..." -ForegroundColor Yellow
        wsl -d Ubuntu --cd (Get-Location).Path -e ./run-docker-tests.sh $Target
        exit $LASTEXITCODE
    }
    Write-Host "[ERROR] Docker Desktop is not running or the engine is still initializing." -ForegroundColor Red
    exit 1
}

Write-Host "Running Docker container for target: $Target..." -ForegroundColor Green

if ($Target -eq "clean") {
    .\clean.ps1
} elseif ($Target -eq "unit") {
    docker compose run --rm test-unit
} elseif ($Target -eq "build") {
    docker compose run --rm build-apk
} elseif ($Target -eq "release") {
    docker compose run --rm build-release
} elseif ($Target -eq "instrumented") {
    docker compose run --rm test-instrumented
} elseif ($Target -eq "all") {
    docker compose run --rm test-all
} else {
    docker compose run --rm test-unit $Target
}
