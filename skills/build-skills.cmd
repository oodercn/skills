@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Skills Unified Build Script
REM Support selective build by module type
REM ========================================

set VERSION=3.0.3
set SKILL_BASE=E:\github\ooder-sdk\skill
set M2_REPO=D:\maven\.m2\repository

echo ========================================
echo   Skills Unified Build Tool v%VERSION%
echo ========================================
echo.

if "%1"=="" goto :menu
set BUILD_TYPE=%1
goto :build

:menu
echo === Build Options ===
echo.
echo   1. all        - Build all modules
echo   2. base       - Build _base modules only (SPI interfaces)
echo   3. drivers    - Build _drivers modules only
echo   4. system     - Build _system modules only
echo   5. common     - Build skill-common only
echo   6. hotplug    - Build skill-hotplug-starter only
echo   7. discovery  - Build skill-discovery only
echo   8. install    - Build skill-install only
echo   9. deploy     - Deploy to central repository
echo   0. exit
echo.
set /p CHOICE="Select build option: "

if "%CHOICE%"=="1" set BUILD_TYPE=all
if "%CHOICE%"=="2" set BUILD_TYPE=base
if "%CHOICE%"=="3" set BUILD_TYPE=drivers
if "%CHOICE%"=="4" set BUILD_TYPE=system
if "%CHOICE%"=="5" set BUILD_TYPE=common
if "%CHOICE%"=="6" set BUILD_TYPE=hotplug
if "%CHOICE%"=="7" set BUILD_TYPE=discovery
if "%CHOICE%"=="8" set BUILD_TYPE=install
if "%CHOICE%"=="9" set BUILD_TYPE=deploy
if "%CHOICE%"=="0" goto :end

if "%BUILD_TYPE%"=="" goto :menu

:build
echo.
echo === Building: %BUILD_TYPE% ===
echo.

REM Set Maven command
set MVN_CMD=mvn clean install -DskipTests

if "%BUILD_TYPE%"=="deploy" (
    set MVN_CMD=mvn clean deploy -DskipTests
    echo Deploy mode: Publishing to central repository
    echo.
)

if "%BUILD_TYPE%"=="all" (
    echo Building all modules...
    echo.
    
    REM Build _base modules
    echo === Building _base modules ===
    for %%m in (ooder-spi-core skill-spi-core skill-spi-llm skill-spi-messaging) do (
        if exist "%SKILL_BASE%\_base\%%m\pom.xml" (
            echo Building: %%m
            cd /d "%SKILL_BASE%\_base\%%m"
            %MVN_CMD%
            echo.
        )
    )
    
    REM Build _drivers modules
    echo === Building _drivers modules ===
    for %%m in (skill-spi skill-llm-base skill-rag skill-org-web) do (
        if exist "%SKILL_BASE%\_drivers\%%m\pom.xml" (
            echo Building: %%m
            cd /d "%SKILL_BASE%\_drivers\%%m"
            %MVN_CMD%
            echo.
        )
    )
    
    REM Build _system modules
    echo === Building _system modules ===
    for %%m in (skill-auth skill-config skill-discovery skill-install skill-menu skill-role skill-llm-chat skill-knowledge-platform skill-capability) do (
        if exist "%SKILL_BASE%\_system\%%m\pom.xml" (
            echo Building: %%m
            cd /d "%SKILL_BASE%\_system\%%m"
            %MVN_CMD%
            echo.
        )
    )
    
    REM Build common modules
    echo === Building common modules ===
    if exist "%SKILL_BASE%\skill-common\pom.xml" (
        echo Building: skill-common
        cd /d "%SKILL_BASE%\skill-common"
        %MVN_CMD%
        echo.
    )
    
    if exist "%SKILL_BASE%\skill-hotplug-starter\pom.xml" (
        echo Building: skill-hotplug-starter
        cd /d "%SKILL_BASE%\skill-hotplug-starter"
        %MVN_CMD%
        echo.
    )
    
    goto :done
)

if "%BUILD_TYPE%"=="base" (
    echo Building _base modules...
    for %%m in (ooder-spi-core skill-spi-core skill-spi-llm skill-spi-messaging) do (
        if exist "%SKILL_BASE%\_base\%%m\pom.xml" (
            echo Building: %%m
            cd /d "%SKILL_BASE%\_base\%%m"
            %MVN_CMD%
            echo.
        )
    )
    goto :done
)

if "%BUILD_TYPE%"=="drivers" (
    echo Building _drivers modules...
    for %%m in (skill-spi skill-llm-base skill-rag skill-org-web) do (
        if exist "%SKILL_BASE%\_drivers\%%m\pom.xml" (
            echo Building: %%m
            cd /d "%SKILL_BASE%\_drivers\%%m"
            %MVN_CMD%
            echo.
        )
    )
    goto :done
)

if "%BUILD_TYPE%"=="system" (
    echo Building _system modules...
    for %%m in (skill-auth skill-config skill-discovery skill-install skill-menu skill-role skill-llm-chat skill-knowledge-platform skill-capability) do (
        if exist "%SKILL_BASE%\_system\%%m\pom.xml" (
            echo Building: %%m
            cd /d "%SKILL_BASE%\_system\%%m"
            %MVN_CMD%
            echo.
        )
    )
    goto :done
)

if "%BUILD_TYPE%"=="common" (
    echo Building skill-common...
    if exist "%SKILL_BASE%\skill-common\pom.xml" (
        cd /d "%SKILL_BASE%\skill-common"
        %MVN_CMD%
    )
    goto :done
)

if "%BUILD_TYPE%"=="hotplug" (
    echo Building skill-hotplug-starter...
    if exist "%SKILL_BASE%\skill-hotplug-starter\pom.xml" (
        cd /d "%SKILL_BASE%\skill-hotplug-starter"
        %MVN_CMD%
    )
    goto :done
)

if "%BUILD_TYPE%"=="discovery" (
    echo Building skill-discovery...
    if exist "%SKILL_BASE%\_system\skill-discovery\pom.xml" (
        cd /d "%SKILL_BASE%\_system\skill-discovery"
        %MVN_CMD%
    )
    goto :done
)

if "%BUILD_TYPE%"=="install" (
    echo Building skill-install...
    if exist "%SKILL_BASE%\_system\skill-install\pom.xml" (
        cd /d "%SKILL_BASE%\_system\skill-install"
        %MVN_CMD%
    )
    goto :done
)

if "%BUILD_TYPE%"=="deploy" (
    echo === Deploying to Central Repository ===
    echo.
    echo This will deploy all modules to Maven Central.
    echo Make sure you have:
    echo   1. GPG key configured
    echo   2. Central portal credentials in settings.xml
    echo.
    set /p CONFIRM="Continue with deploy? (Y/N): "
    if /i not "!CONFIRM!"=="Y" goto :end
    
    REM Deploy in order: base - drivers - system - common
    echo.
    echo === Deploying _base modules ===
    for %%m in (ooder-spi-core skill-spi-core skill-spi-llm skill-spi-messaging) do (
        if exist "%SKILL_BASE%\_base\%%m\pom.xml" (
            echo Deploying: %%m
            cd /d "%SKILL_BASE%\_base\%%m"
            mvn clean deploy -DskipTests
            echo.
        )
    )
    
    echo === Deploying _drivers modules ===
    for %%m in (skill-spi skill-llm-base skill-rag skill-org-web) do (
        if exist "%SKILL_BASE%\_drivers\%%m\pom.xml" (
            echo Deploying: %%m
            cd /d "%SKILL_BASE%\_drivers\%%m"
            mvn clean deploy -DskipTests
            echo.
        )
    )
    
    echo === Deploying _system modules ===
    for %%m in (skill-auth skill-config skill-discovery skill-install skill-menu skill-role skill-llm-chat skill-knowledge-platform skill-capability) do (
        if exist "%SKILL_BASE%\_system\%%m\pom.xml" (
            echo Deploying: %%m
            cd /d "%SKILL_BASE%\_system\%%m"
            mvn clean deploy -DskipTests
            echo.
        )
    )
    
    echo === Deploying common modules ===
    if exist "%SKILL_BASE%\skill-common\pom.xml" (
        echo Deploying: skill-common
        cd /d "%SKILL_BASE%\skill-common"
        mvn clean deploy -DskipTests
        echo.
    )
    
    if exist "%SKILL_BASE%\skill-hotplug-starter\pom.xml" (
        echo Deploying: skill-hotplug-starter
        cd /d "%SKILL_BASE%\skill-hotplug-starter"
        mvn clean deploy -DskipTests
        echo.
    )
    
    goto :done
)

echo Unknown build type: %BUILD_TYPE%
goto :menu

:done
echo.
echo ========================================
echo   Build completed!
echo ========================================
echo.

:end
cd /d "%SKILL_BASE%"
pause