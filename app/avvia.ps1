<#
  ContiInTasca - avvio della demo (backend Spring Boot + frontend Angular).

  Uso (dalla radice del repository):
    powershell -ExecutionPolicy Bypass -File app\avvia.ps1            avvia tutto e apre il browser
    powershell -ExecutionPolicy Bypass -File app\avvia.ps1 -Ferma     ferma backend e frontend
#>
param(
    [switch]$Ferma,
    [int]$AttesaMassimaSecondi = 180
)

$ErrorActionPreference = 'Stop'
$backendDir  = Join-Path $PSScriptRoot 'backend'
$frontendDir = Join-Path $PSScriptRoot 'frontend'
$portaBackend  = 8080
$portaFrontend = 4200
$url = "http://localhost:$portaFrontend"

function Test-Porta([int]$porta) {
    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $async = $client.BeginConnect('127.0.0.1', $porta, $null, $null)
        if ($async.AsyncWaitHandle.WaitOne(500)) { $client.EndConnect($async); return $true }
        return $false
    } catch { return $false } finally { $client.Close() }
}

function Stop-Porta([int]$porta) {
    $pids = Get-NetTCPConnection -LocalPort $porta -State Listen -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($p in $pids) {
        try { Stop-Process -Id $p -Force -ErrorAction Stop; Write-Host "  fermato processo $p sulla porta $porta" }
        catch { Write-Warning "  impossibile fermare il processo $p sulla porta $porta" }
    }
    if (-not $pids) { Write-Host "  nessun processo sulla porta $porta" }
}

function Wait-Porta([int]$porta, [string]$nome) {
    $inizio = Get-Date
    Write-Host -NoNewline "Attendo $nome sulla porta $porta "
    while (-not (Test-Porta $porta)) {
        if (((Get-Date) - $inizio).TotalSeconds -gt $AttesaMassimaSecondi) {
            Write-Host ''
            throw "$nome non risponde dopo $AttesaMassimaSecondi secondi: controlla la sua finestra per l'errore."
        }
        Write-Host -NoNewline '.'
        Start-Sleep -Seconds 2
    }
    Write-Host ' pronto.'
}

if ($Ferma) {
    Write-Host 'Fermo ContiInTasca...'
    Stop-Porta $portaFrontend
    Stop-Porta $portaBackend
    return
}

Write-Host '== ContiInTasca: avvio della demo ==' -ForegroundColor Magenta

foreach ($cmd in 'java', 'mvn.cmd', 'node', 'npm.cmd', 'npx.cmd') {
    if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) { throw "Prerequisito mancante: $cmd non trovato nel PATH." }
}
foreach ($dir in $backendDir, $frontendDir) {
    if (-not (Test-Path -LiteralPath $dir)) { throw "Cartella non trovata: $dir" }
}
foreach ($porta in $portaBackend, $portaFrontend) {
    if (Test-Porta $porta) {
        throw "La porta $porta e' gia' occupata (app gia' avviata o test in corso). Usa: powershell -ExecutionPolicy Bypass -File app\avvia.ps1 -Ferma"
    }
}

if (-not (Test-Path -LiteralPath (Join-Path $frontendDir 'node_modules'))) {
    Write-Host 'Installo le dipendenze del frontend (solo la prima volta, qualche minuto)...'
    Push-Location -LiteralPath $frontendDir
    try {
        & npm.cmd install --no-audit --no-fund
        if ($LASTEXITCODE -ne 0) { throw "npm install non riuscito (codice $LASTEXITCODE)." }
    } finally { Pop-Location }
}

# Su questo PC Java non riesce a creare il canale interno di NIO (socket AF_UNIX) nella cartella temporanea di Windows:
# senza questa cartella alternativa Tomcat fallisce con "Unable to establish loopback connection".
$cartellaSocket = Join-Path $env:USERPROFILE '.itm-tmp'
New-Item -ItemType Directory -Force -Path $cartellaSocket | Out-Null
$avvioBackend  = "`$host.UI.RawUI.WindowTitle = 'ContiInTasca - backend'; Set-Location -LiteralPath '$backendDir'; mvn.cmd spring-boot:run '-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=$cartellaSocket'"
$avvioFrontend = "`$host.UI.RawUI.WindowTitle = 'ContiInTasca - frontend'; Set-Location -LiteralPath '$frontendDir'; npx.cmd ng serve"

Write-Host 'Avvio il backend in una nuova finestra...'
Start-Process powershell -ArgumentList '-NoExit', '-NoProfile', '-ExecutionPolicy', 'Bypass', '-Command', $avvioBackend | Out-Null
Write-Host 'Avvio il frontend in una nuova finestra...'
Start-Process powershell -ArgumentList '-NoExit', '-NoProfile', '-ExecutionPolicy', 'Bypass', '-Command', $avvioFrontend | Out-Null

Wait-Porta $portaBackend 'il backend'
Wait-Porta $portaFrontend 'il frontend'

Start-Process $url
Write-Host ''
Write-Host "Pronto: $url" -ForegroundColor Green
Write-Host 'Dati di esempio da caricare in Amministrazione:'
Write-Host ('  ' + (Join-Path (Split-Path -Parent $PSScriptRoot) 'agents\skills\formato-csv-istat\esempio_istat.csv'))
Write-Host 'Per fermare tutto: powershell -ExecutionPolicy Bypass -File app\avvia.ps1 -Ferma'
