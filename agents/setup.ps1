# Collega .claude/agents, .claude/skills e .claude/commands alle cartelle in agents/ (fonte unica delle istruzioni).
# Eseguire dalla radice del repository: powershell -ExecutionPolicy Bypass -File agents\setup.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$links = @{ 'agents' = 'subagents'; 'skills' = 'skills'; 'commands' = 'commands' }

New-Item -ItemType Directory -Force -Path (Join-Path $root '.claude') | Out-Null
foreach ($name in $links.Keys) {
    $link = Join-Path $root ".claude\$name"
    $target = Join-Path $PSScriptRoot $links[$name]
    if (Test-Path $link) {
        $item = Get-Item $link -Force
        if ($item.LinkType -eq 'Junction') { Write-Host "OK   .claude\$name esiste gia'"; continue }
        throw ".claude\$name esiste ma non e' un collegamento: spostalo e rilancia lo script."
    }
    New-Item -ItemType Junction -Path $link -Target $target | Out-Null
    Write-Host "OK   .claude\$name -> agents\$($links[$name])"
}
