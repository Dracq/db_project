$ErrorActionPreference = "Stop"

Write-Host "Getting Auth Token..."
$loginBody = @{
    email = "trader@db.com"
    password = "trader123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token

Write-Host "Token obtained successfully. Starting load test..."

$tradePayload = @{
    tradeRef = "PRF-20260730-0000"
    instrumentId = 1
    counterpartyId = 1
    assetClass = "EQUITY"
    side = "BUY"
    quantity = 100
    price = 245.5
    tradeDate = "2026-06-02"
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $token"
}

$successCount = 0
$failCount = 0

Write-Host "Running 100 requests sequentially..."
for ($i = 1; $i -le 100; $i++) {
    $formattedIndex = "{0:D4}" -f $i
    $modifiedPayload = $tradePayload -replace '0000', $formattedIndex
    
    try {
        Invoke-RestMethod -Uri "http://localhost:8080/api/v1/trades" -Method POST -Body $modifiedPayload -ContentType "application/json" -Headers $headers | Out-Null
        $successCount++
    } catch {
        $failCount++
    }
}

Write-Host "Load test completed!"
Write-Host "Successful requests: $successCount"
Write-Host "Failed requests: $failCount"
Write-Host "Check your Grafana dashboard at http://localhost:3000 to see the spikes in throughput and latency."
