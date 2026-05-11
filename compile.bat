@echo off
REM 编译所有 Java 源文件
echo === 编译 Java 源文件 ===
javac -encoding UTF-8 -d out -sourcepath src src/com/practice/HelloWorld.java
if %errorlevel% equ 0 (
    echo === 编译成功！ ===
    echo 运行: java -cp out com.practice.HelloWorld
) else (
    echo === 编译失败，请检查错误信息 ===
)
