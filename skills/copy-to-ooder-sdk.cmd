@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Skills Modules Unified Management Script
REM Copy modules from apexos to ooder-sdk/skill
REM ========================================

set VERSION=3.0.3
set SOURCE_BASE=e:\apex\apexos\skills
set TARGET_BASE=E:\github\ooder-sdk\skill

echo ========================================
echo   Skills Modules Unified Manager v%VERSION%
echo ========================================
echo.

echo Source: %SOURCE_BASE%
echo Target: %TARGET_BASE%
echo.

echo === Modules to Copy ===
echo.
echo [_base] SPI Base Modules:
echo   - ooder-spi-core
echo   - skill-spi-core
echo   - skill-spi-llm
echo   - skill-spi-messaging
echo.
echo [_drivers] Driver Modules:
echo   - skill-spi
echo   - skill-llm-base
echo   - skill-rag
echo   - skill-org-web
echo.
echo [_system] System Service Modules:
echo   - skill-auth
echo   - skill-config
echo   - skill-discovery
echo   - skill-install
echo   - skill-menu
echo   - skill-role
echo   - skill-llm-chat
echo   - skill-knowledge-platform
echo   - skill-capability
echo.

set /p CONFIRM="Continue to copy modules? (Y/N): "
if /i not "%CONFIRM%"=="Y" (
    echo Cancelled.
    goto :end
)

echo.
echo Starting copy...
echo.

REM Create target directories
if not exist "%TARGET_BASE%\_base" mkdir "%TARGET_BASE%\_base"
if not exist "%TARGET_BASE%\_drivers" mkdir "%TARGET_BASE%\_drivers"
if not exist "%TARGET_BASE%\_system" mkdir "%TARGET_BASE%\_system"

REM Copy _base modules
echo === Copying _base modules ===
for %%m in (ooder-spi-core skill-spi-core skill-spi-llm skill-spi-messaging) do (
    set SRC=%SOURCE_BASE%\_base\%%m
    set DST=%TARGET_BASE%\_base\%%m
    if exist "!SRC!" (
        echo Copying: %%m
        if exist "!DST!" rmdir /s /q "!DST!"
        xcopy "!SRC!" "!DST!\" /E /I /Q >nul
        echo   Done: !DST!
    ) else (
        echo   Skip: %%m (not found)
    )
)

REM Copy _drivers modules
echo.
echo === Copying _drivers modules ===

if exist "%SOURCE_BASE%\_drivers\spi\skill-spi" (
    echo Copying: skill-spi
    if exist "%TARGET_BASE%\_drivers\skill-spi" rmdir /s /q "%TARGET_BASE%\_drivers\skill-spi"
    xcopy "%SOURCE_BASE%\_drivers\spi\skill-spi" "%TARGET_BASE%\_drivers\skill-spi\" /E /I /Q >nul
    echo   Done: %TARGET_BASE%\_drivers\skill-spi
)

if exist "%SOURCE_BASE%\_drivers\llm\skill-llm-base" (
    echo Copying: skill-llm-base
    if exist "%TARGET_BASE%\_drivers\skill-llm-base" rmdir /s /q "%TARGET_BASE%\_drivers\skill-llm-base"
    xcopy "%SOURCE_BASE%\_drivers\llm\skill-llm-base" "%TARGET_BASE%\_drivers\skill-llm-base\" /E /I /Q >nul
    echo   Done: %TARGET_BASE%\_drivers\skill-llm-base
)

if exist "%SOURCE_BASE%\_drivers\rag\skill-rag" (
    echo Copying: skill-rag
    if exist "%TARGET_BASE%\_drivers\skill-rag" rmdir /s /q "%TARGET_BASE%\_drivers\skill-rag"
    xcopy "%SOURCE_BASE%\_drivers\rag\skill-rag" "%TARGET_BASE%\_drivers\skill-rag\" /E /I /Q >nul
    echo   Done: %TARGET_BASE%\_drivers\skill-rag
)

if exist "%SOURCE_BASE%\_drivers\org\skill-org-web" (
    echo Copying: skill-org-web
    if exist "%TARGET_BASE%\_drivers\skill-org-web" rmdir /s /q "%TARGET_BASE%\_drivers\skill-org-web"
    xcopy "%SOURCE_BASE%\_drivers\org\skill-org-web" "%TARGET_BASE%\_drivers\skill-org-web\" /E /I /Q >nul
    echo   Done: %TARGET_BASE%\_drivers\skill-org-web
)

REM Copy _system modules
echo.
echo === Copying _system modules ===
for %%m in (skill-auth skill-config skill-discovery skill-install skill-menu skill-role skill-llm-chat skill-knowledge-platform skill-capability) do (
    set SRC=%SOURCE_BASE%\_system\%%m
    set DST=%TARGET_BASE%\_system\%%m
    if exist "!SRC!" (
        echo Copying: %%m
        if exist "!DST!" rmdir /s /q "!DST!"
        xcopy "!SRC!" "!DST!\" /E /I /Q >nul
        echo   Done: !DST!
    ) else (
        echo   Skip: %%m (not found)
    )
)

echo.
echo ========================================
echo   Copy completed!
echo ========================================
echo.
echo Target structure:
echo   %TARGET_BASE%
echo   +-- _base/
echo       +-- ooder-spi-core
echo       +-- skill-spi-core
echo       +-- skill-spi-llm
echo       +-- skill-spi-messaging
echo   +-- _drivers/
echo       +-- skill-spi
echo       +-- skill-llm-base
echo       +-- skill-rag
echo       +-- skill-org-web
echo   +-- _system/
echo       +-- skill-auth
echo       +-- skill-config
echo       +-- skill-discovery
echo       +-- skill-install
echo       +-- skill-menu
echo       +-- skill-role
echo       +-- skill-llm-chat
echo       +-- skill-knowledge-platform
echo       +-- skill-capability
echo   +-- skill-common/
echo   +-- skill-hotplug-starter/
echo.

:end
pause