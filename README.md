# Android 悬浮时钟

带秒数显示的悬浮窗时钟应用。

## 功能

- 全屏时钟显示（时:分:秒）
- 悬浮小窗口（可拖动到屏幕任意位置）
- 悬浮窗在其他应用上层持续显示
- 日期、星期显示

## 技术栈

- Kotlin
- Jetpack Compose
- WindowManager (悬浮窗)
- Foreground Service

## 编译运行

### 要求

- Android Studio Hedgehog (2023.1.1) 或更高
- JDK 17
- Android SDK 34
- minSdk 26 (Android 8.0+)

### 步骤

1. Android Studio 打开本项目
2. 等待 Gradle Sync 完成
3. 连接手机（开启 USB 调试）或启动模拟器
4. 点击运行按钮 ▶️

### 手动编译 APK

```bash
# Windows
.\gradlew assembleDebug

# APK 输出路径
app/build/outputs/apk/debug/app-debug.apk
```

## 使用说明

1. 打开 App，看到全屏时钟
2. 点击「开启悬浮时钟」
3. 首次使用需授权「显示在其他应用上层」
4. 小窗口出现后，可拖动到任意位置
5. 返回桌面或打开其他 App，悬浮时钟依然显示

## 权限

- `SYSTEM_ALERT_WINDOW` — 悬浮窗权限
- `FOREGROUND_SERVICE` — 前台服务保活
