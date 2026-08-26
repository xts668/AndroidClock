@echo off
chcp 65001 >nul
title 推送到 GitHub
echo ==========================================
echo    Android 悬浮时钟 - 推送到 GitHub
echo ==========================================
echo.

REM 检查 git
git --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到 git 命令
    echo 请从 https://git-scm.com/download/win 安装 Git
    pause
    exit /b 1
)

set /p REPO_URL="请输入你的 GitHub 仓库地址 (如: https://github.com/用户名/AndroidClock.git): "

if "%REPO_URL%"=="" (
    echo [错误] 仓库地址不能为空
    pause
    exit /b 1
)

echo.
echo [1/4] 初始化 Git 仓库...
git init

echo.
echo [2/4] 添加文件到暂存区...
git add .

echo.
echo [3/4] 提交代码...
git commit -m "Initial commit: Android floating clock"

echo.
echo [4/4] 推送到 GitHub...
git branch -M main
git remote add origin %REPO_URL% 2>nul
git push -u origin main

if errorlevel 1 (
    echo.
    echo [提示] 推送失败，可能原因：
    echo   1. 仓库地址错误
    echo   2. 未登录 GitHub（请先用 git config 配置用户名和邮箱）
    echo   3. 网络问题
    echo.
    echo 解决方法：
    echo   git config --global user.name "你的用户名"
    echo   git config --global user.email "你的邮箱"
    echo   然后重新运行本脚本
    pause
    exit /b 1
)

echo.
echo ==========================================
echo       推送成功！
echo ==========================================
echo.
echo 接下来：
echo   1. 打开浏览器访问你的 GitHub 仓库
echo   2. 点击顶部的 "Actions" 标签
echo   3. 等待 3-5 分钟编译完成
echo   4. 在 workflow 页面下载 app-debug.zip
echo.
pause
