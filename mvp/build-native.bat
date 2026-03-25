@echo off
setlocal

REM GraalVM Native Image Build Script

REM 1. Change to project directory
E:
cd \github\ooder-skills\mvp

REM 2. Set GraalVM environment
set JAVA_HOME=D:\graalvm\graalvm-jdk-21.0.2+13.1
set GRAALVM_HOME=D:\graalvm\graalvm-jdk-21.0.2+13.1
set PATH=%JAVA_HOME%\bin;%PATH%
set TEMP=E:\temp\native-image
set TMP=E:\temp\native-image

REM 3. Verify GraalVM
echo ========================================
echo Using JAVA_HOME: %JAVA_HOME%
echo ========================================
"%JAVA_HOME%\bin\java" -version
echo ========================================

REM 4. Set Visual Studio environment
if exist "E:\vs\VC\Auxiliary\Build\vcvarsall.bat" (
    echo Setting up Visual Studio environment...
    call "E:\vs\VC\Auxiliary\Build\vcvarsall.bat" x64
) else (
    echo ERROR: Visual Studio vcvarsall.bat not found!
    exit /b 1
)

REM 5. Set memory options
set MAVEN_OPTS=-Xmx12g

REM 6. Verify current directory
echo Current directory: %CD%
if not exist pom.xml (
    echo ERROR: pom.xml not found!
    exit /b 1
)

REM 7. Run Maven build
echo Starting Maven build...
call mvn clean package -Pnative -DskipTests

endlocal
