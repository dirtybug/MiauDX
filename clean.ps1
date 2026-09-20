# MiauDX - Complete cleanup PowerShell script
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "            MiauDX - Workspace Clean                " -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/4] Stopping Gradle daemons..." -ForegroundColor Yellow
if (Test-Path ".\gradlew.bat") {
    & .\gradlew.bat --stop 2>$null
}

Write-Host "[2/4] Removing build directories..." -ForegroundColor Yellow
@("build", "app\build") | ForEach-Object {
    if (Test-Path $_) {
        Remove-Item -Path $_ -Recurse -Force -ErrorAction SilentlyContinue
        Write-Host "  - Removed $_" -ForegroundColor DarkGray
    }
}

Write-Host "[3/4] Removing .gradle and .idea directories..." -ForegroundColor Yellow
@(".gradle", ".idea") | ForEach-Object {
    if (Test-Path $_) {
        Remove-Item -Path $_ -Recurse -Force -ErrorAction SilentlyContinue
        Write-Host "  - Removed $_" -ForegroundColor DarkGray
    }
}

Write-Host "[4/4] Removing residual cache and iml files..." -ForegroundColor Yellow
Get-ChildItem -Path . -Filter "*.iml" -Recurse -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "====================================================" -ForegroundColor Green
Write-Host "  Workspace cleaned successfully!                   " -ForegroundColor Green
Write-Host "====================================================" -ForegroundColor Green
Write-Host ""
