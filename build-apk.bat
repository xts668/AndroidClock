@echo off
chcp 65001 >nul
title 一键编译 APK
echo ==========================================
echo      Android 悬浮时钟 - 一键编译
echo ==========================================
echo.

REM 检查 Java 环境
if "%JAVA_HOME%"=="" (
    echo [错误] 未设置 JAVA_HOME 环境变量
    echo 请安装 JDK 17 并配置环境变量
    pause
    exit /b 1
)

java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] Java 未安装或未添加到 PATH
    pause
    exit /b 1
)
echo [✓] Java 环境正常

REM 检查 Android SDK
if "%ANDROID_SDK_ROOT%"=="" (
    if "%ANDROID_HOME%"=="" (
        echo [警告] 未找到 ANDROID_SDK_ROOT 或 ANDROID_HOME
        echo 尝试自动检测...
        
        REM 常见安装路径检测
        if exist "%LOCALAPPDATA%\Android\Sdk" (
            set "ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk"
            echo [✓] 自动检测到 SDK: %ANDROID_SDK_ROOT%
        ) else if exist "C:\Android\Sdk" (
            set "ANDROID_SDK_ROOT=C:\Android\Sdk"
            echo [✓] 自动检测到 SDK: %ANDROID_SDK_ROOT%
        ) else (
            echo [错误] 未找到 Android SDK
            echo 请安装 Android Studio 或手动设置 ANDROID_SDK_ROOT
            pause
            exit /b 1
        )
    ) else (
        set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
    )
)
echo [✓] Android SDK: %ANDROID_SDK_ROOT%

REM 检查 local.properties
if not exist "local.properties" (
    if exist "local.properties.example" (
        echo [提示] 正在从模板创建 local.properties...
        copy /y local.properties.example local.properties >nul
        echo [!] 请编辑 local.properties，将 SDK 路径改为你的实际路径
        notepad local.properties
        echo 保存后按任意键继续...
        pause >nul
    ) else (
        echo sdk.dir=%ANDROID_SDK_ROOT% > local.properties
    )
)

REM 检查 gradlew
if not exist "gradlew.bat" (
    echo [提示] 未找到 gradlew.bat，正在生成 Gradle Wrapper...
    call gradle wrapper --gradle-version 8.2
    if errorlevel 1 (
        echo [错误] Gradle Wrapper 生成失败，请确保已安装 Gradle
        pause
        exit /b 1
    )
)

echo.
echo ==========================================
echo          开始编译 Debug APK
echo ==========================================
echo.

call gradlew.bat assembleDebug

if errorlevel 1 (
    echo.
    echo [错误] 编译失败！
    pause
    exit /b 1
)

echo.
echo ==========================================
echo           编译成功！
echo ==========================================
echo.
echo APK 文件位置:
echo   app\build\outputs\apk\debug\app-debug.apk
echo.

REM 检查是否连接了手机
echo 检查设备连接...
adb devices >nul 2>&1
if errorlevel 1 (
    echo [提示] 未找到 adb，跳过自动安装
) else (
    adb get-state >nul 2>&1
    if not errorlevel 1 (
        echo [✓] 检测到设备，正在安装...
        adb install -r "app\build\outputs\apk\debug\app-debug.apk"
        if not errorlevel 1 (
            echo [✓] 安装成功！
        ) else (
            echo [!] 安装失败，请检查设备是否允许安装
        )
    ) else (
        echo [提示] 未连接设备，APK 已生成但未安装
    )
)

echo.
pause
