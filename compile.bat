@echo off
REM 编译所有 Java 源文件
echo === 编译 Java 源文件 ===
javac -encoding UTF-8 -d out -sourcepath src src/com/practice/**/*.java
if %errorlevel% equ 0 (
    echo === 编译成功！ ===
    echo.
    echo 运行示例:
    echo   java -cp out com.practice.basics.BasicsDemo
    echo   java -cp out com.practice.controlflow.ControlFlowDemo
    echo   java -cp out com.practice.methods.MethodsDemo
    echo   java -cp out com.practice.arrays.ArraysDemo
    echo   java -cp out com.practice.oop.OopDemo
    echo   java -cp out com.practice.inheritance.InheritanceDemo
    echo   java -cp out com.practice.interfaces.InterfaceDemo
    echo   java -cp out com.practice.collections.CollectionsDemo
    echo   java -cp out com.practice.exceptions.ExceptionsDemo
    echo   java -cp out com.practice.java17.Java17Demo
) else (
    echo === 编译失败，请检查错误信息 ===
)
