@echo off
chcp 65001 >nul
call compile.bat
if %errorlevel% equ 0 (
    echo.
    echo === Running ===
    java -Dfile.encoding=UTF-8 -cp out com.practice.HelloWorld
)
