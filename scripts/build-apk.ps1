$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$gradlew = Join-Path $root "gradlew.bat"
if (-not (Test-Path $gradlew)) {
    throw "gradlew.bat not found at $gradlew"
}

Write-Host "Syncing Gradle dependencies..."
& $gradlew --refresh-dependencies
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Building release APK..."
& $gradlew :app:assembleRelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apkPath = Join-Path $root "app\build\outputs\apk\release"
Write-Host "Build finished. APK output folder: $apkPath"
