$ErrorActionPreference = "Stop"

$ToolsDir = "tools"
if (!(Test-Path $ToolsDir)) {
    New-Item -ItemType Directory -Force -Path $ToolsDir | Out-Null
}

Write-Host "Downloading Prometheus..."
$PromZip = "$ToolsDir\prometheus.zip"
if (!(Test-Path $PromZip)) {
    Invoke-WebRequest -Uri "https://github.com/prometheus/prometheus/releases/download/v2.54.1/prometheus-2.54.1.windows-amd64.zip" -OutFile $PromZip
}
Write-Host "Extracting Prometheus..."
if (!(Test-Path "$ToolsDir\prometheus")) {
    Expand-Archive -Path $PromZip -DestinationPath $ToolsDir -Force
    Rename-Item -Path "$ToolsDir\prometheus-2.54.1.windows-amd64" -NewName "prometheus"
}

Write-Host "Downloading Grafana..."
$GrafanaZip = "$ToolsDir\grafana.zip"
if (!(Test-Path $GrafanaZip)) {
    Invoke-WebRequest -Uri "https://dl.grafana.com/oss/release/grafana-11.2.0.windows-amd64.zip" -OutFile $GrafanaZip
}
Write-Host "Extracting Grafana..."
if (!(Test-Path "$ToolsDir\grafana")) {
    Expand-Archive -Path $GrafanaZip -DestinationPath $ToolsDir -Force
    Rename-Item -Path "$ToolsDir\grafana-11.2.0" -NewName "grafana"
}

Write-Host "Configuring Grafana Provisioning..."
Copy-Item -Path "monitoring\grafana\provisioning\*" -Destination "$ToolsDir\grafana\conf\provisioning\" -Recurse -Force

Write-Host "========================================="
Write-Host "Setup Complete!"
Write-Host "You can start the observability stack by running:"
Write-Host ""
Write-Host "1. Start Prometheus:"
Write-Host "   cd tools\prometheus"
Write-Host "   .\prometheus.exe --config.file=..\..\monitoring\prometheus\prometheus.yml"
Write-Host ""
Write-Host "2. Start Grafana:"
Write-Host "   cd tools\grafana\bin"
Write-Host "   .\grafana-server.exe"
Write-Host ""
Write-Host "3. Start Backend:"
Write-Host "   cd backend"
Write-Host "   .\mvnw.cmd spring-boot:run"
Write-Host "========================================="
