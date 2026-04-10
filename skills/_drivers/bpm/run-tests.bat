@echo off
chcp 65001 >nul
echo ==========================================
echo BPM 扩展属性修复测试运行脚本
echo ==========================================
echo.

cd /d e:\github\ooder-skills\skills\_drivers\bpm\bpmserver

echo [1/3] 编译项目...
call mvn clean compile -DskipTests -q
if %errorlevel% neq 0 (
    echo [错误] 编译失败！
    pause
    exit /b 1
)
echo [完成] 编译成功
echo.

echo [2/3] 运行XPDL兼容性测试...
call mvn test -Dtest=XpdlCompatibilityTest -q
if %errorlevel% neq 0 (
    echo [警告] XPDL兼容性测试有失败项
) else (
    echo [完成] XPDL兼容性测试通过
)
echo.

echo [3/3] 运行集成测试...
call mvn test -Dtest=ProcessDefManagerServiceIntegrationTest -q
if %errorlevel% neq 0 (
    echo [警告] 集成测试有失败项，请检查数据库连接
) else (
    echo [完成] 集成测试通过
)
echo.

echo ==========================================
echo 测试执行完成
echo ==========================================
echo.
echo 测试报告位置:
echo   target\surefire-reports\
echo.
pause
