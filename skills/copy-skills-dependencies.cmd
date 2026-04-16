@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM ========================================
REM Skills Dependencies Management Script
REM ========================================
REM 
REM This script manages skill module dependencies for ApexOS.
REM 
REM Modules are organized into three categories:
REM   1. Central Repo - Already published to Maven Central
REM   2. Local Skills - Already in apexos/skills directory
REM   3. External Copy - Need to copy from ooder-sdk
REM
REM ========================================

set VERSION=3.0.3
set SOURCE_PATH=E:\github\ooder-sdk\skill
set TARGET_PATH=e:\apex\apexos\skills\_external

echo ========================================
echo   Skills Dependencies Manager v%VERSION%
echo ========================================
echo.

echo === Central Repository Modules (Published) ===
echo These modules are available from Maven Central:
echo   - skill-common
echo   - skill-hotplug-starter
echo   - skills-framework
echo   - scene-engine
echo   - skill-discovery
echo   - skill-install
echo.
echo Maven dependency example:
echo   ^<dependency^>
echo     ^<groupId^>net.ooder^</groupId^>
echo     ^<artifactId^>skill-common^</artifactId^>
echo     ^<version^>%VERSION%^</version^>
echo   ^</dependency^>
echo.

echo === Local Skills Modules (In apexos/skills) ===
echo These modules are already in the apexos project:
echo.
echo _base (SPI Base Modules):
echo   - ooder-spi-core
echo   - skill-spi-core
echo   - skill-spi-llm
echo   - skill-spi-messaging
echo.
echo _drivers (Driver Modules):
echo   - skill-spi
echo   - skill-llm-base
echo   - skill-rag
echo   - skill-org-web
echo.
echo _system (System Service Modules):
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

echo === External Copy (From ooder-sdk) ===
echo Only these modules need to be copied from ooder-sdk:
echo   - skill-common (already published to central)
echo   - skill-hotplug-starter (already published to central)
echo.
echo No additional copy needed - all dependencies are available!
echo.

echo ========================================
echo   Dependency Configuration Guide
echo ========================================
echo.
echo For skill development, use this pom.xml configuration:
echo.
echo ^<!-- Parent POM --^>
echo ^<parent^>
echo   ^<groupId^>net.ooder^</groupId^>
echo   ^<artifactId^>ooder-sdk-parent^</artifactId^>
echo   ^<version^>%VERSION%^</version^>
echo ^</parent^>
echo.
echo ^<!-- Common Dependencies --^>
echo ^<dependencies^>
echo   ^<dependency^>
echo     ^<groupId^>org.springframework.boot^</groupId^>
echo     ^<artifactId^>spring-boot-starter-web^</artifactId^>
echo     ^<scope^>provided^</scope^>
echo   ^</dependency^>
echo   ^<dependency^>
echo     ^<groupId^>net.ooder^</groupId^>
echo     ^<artifactId^>skill-common^</artifactId^>
echo     ^<version^>%VERSION%^</version^>
echo     ^<scope^>provided^</scope^>
echo   ^</dependency^>
echo   ^<dependency^>
echo     ^<groupId^>net.ooder^</groupId^>
echo     ^<artifactId^>skill-hotplug-starter^</artifactId^>
echo     ^<version^>%VERSION%^</version^>
echo     ^<scope^>provided^</scope^>
echo   ^</dependency^>
echo ^</dependencies^>
echo.

pause