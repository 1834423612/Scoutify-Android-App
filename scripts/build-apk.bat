@echo off
setlocal

set ROOT=%~dp0..
set SCRIPT=%ROOT%\scripts\build-apk.ps1

if not exist "%SCRIPT%" (
  echo build-apk.ps1 not found at %SCRIPT%
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%"
exit /b %ERRORLEVEL%
