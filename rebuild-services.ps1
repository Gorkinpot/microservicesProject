# Script to rebuild all microservices
Write-Host "Starting rebuild of all microservices..." -ForegroundColor Green

$services = @(
    "CartService",
    "BookingService",
    "CatalogService",
    "CommentService",
    "DocumentService",
    "NotificationService",
    "PaymentService",
    "RoomService"
)

foreach ($service in $services) {
    Write-Host "`nRebuilding $service..." -ForegroundColor Yellow
    Push-Location $service
    if (Test-Path "gradlew.bat") {
        & .\gradlew.bat clean build shadowJar
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Error building $service" -ForegroundColor Red
            Pop-Location
            exit 1
        }
    } else {
        Write-Host "gradlew.bat not found in $service" -ForegroundColor Red
        Pop-Location
        continue
    }
    Pop-Location
    Write-Host "$service rebuilt successfully" -ForegroundColor Green
}

Write-Host "`nAll services rebuilt. Building Docker images..." -ForegroundColor Green
docker compose -f docker-compose.yml build

Write-Host "`nRestarting services..." -ForegroundColor Green
docker compose -f docker-compose.yml up -d

Write-Host "`nDone! All services rebuilt and restarted." -ForegroundColor Green
