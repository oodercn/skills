@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo   Skills 打包脚本
echo   打包所有 skills 到 plugins 目录
echo ========================================
echo.

set MAVEN_REPO=D:\maven\.m2\repository
set PLUGINS_DIR=%~dp0..\..\plugins
set DEV_DIR=%~dp0

echo Maven 仓库: %MAVEN_REPO%
echo Plugins 目录: %PLUGINS_DIR%
echo.

if not exist "%PLUGINS_DIR%" mkdir "%PLUGINS_DIR%"

echo [1/3] 打包 _system 目录下的 skills...
for /d %%s in ("%DEV_DIR%_system\skill-*") do (
    if exist "%%s\pom.xml" (
        echo   打包: %%~nxs
        cd /d "%%s"
        call mvn clean package -Dmaven.repo.local=%MAVEN_REPO% -DskipTests -q
        if exist "target\*.jar" (
            for %%j in (target\*.jar) do (
                copy /Y "%%j" "%PLUGINS_DIR%\" >nul
                echo     - 已复制: %%~nxj
            )
        )
    )
)

echo.
echo [2/3] 打包 _business 目录下的 skills...
for /d %%s in ("%DEV_DIR%_business\skill-*") do (
    if exist "%%s\pom.xml" (
        echo   打包: %%~nxs
        cd /d "%%s"
        call mvn clean package -Dmaven.repo.local=%MAVEN_REPO% -DskipTests -q
        if exist "target\*.jar" (
            for %%j in (target\*.jar) do (
                copy /Y "%%j" "%PLUGINS_DIR%\" >nul
                echo     - 已复制: %%~nxj
            )
        )
    )
)

echo.
echo [3/3] 打包 _drivers 目录下的 skills...
for /d /r "%DEV_DIR%_drivers" %%s in (.) do (
    if exist "%%s\pom.xml" (
        echo   打包: %%~nxs
        cd /d "%%s"
        call mvn clean package -Dmaven.repo.local=%MAVEN_REPO% -DskipTests -q
        if exist "target\*.jar" (
            for %%j in (target\*.jar) do (
                copy /Y "%%j" "%PLUGINS_DIR%\" >nul
                echo     - 已复制: %%~nxj
            )
        )
    )
)

cd /d "%DEV_DIR%"
echo.
echo ========================================
echo   打包完成！
echo   Plugins 目录: %PLUGINS_DIR%
echo ========================================
echo.
dir /b "%PLUGINS_DIR%\*.jar"
echo.
pause
