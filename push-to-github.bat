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
echo ==========================================
echo  重要提示：GitHub 需要使用 Token 登录
echo ==========================================
echo.
echo GitHub 已于 2021 年停止支持密码登录，
echo 推送时需要使用 Personal Access Token。
echo.
echo 如果你还没有 Token，请：
echo   1. 打开 https://github.com/settings/tokens/new
echo   2. Note 填 AndroidClock
echo   3. 勾选 repo 权限
echo   4. 点击 Generate token
echo   5. 复制生成的 token（ghp_开头）
echo.
echo 准备好后按任意键继续...
pause >nul

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
git remote remove origin 2>nul
git remote add origin %REPO_URL%

echo.
echo ------------------------------------------
echo  接下来会提示输入用户名和密码：
echo    用户名：你的 GitHub 用户名
echo    密码：粘贴 Personal Access Token
echo ------------------------------------------
echo.

git push -u origin main

if errorlevel 1 (
    echo.
    echo [提示] 推送失败，可能原因：
    echo   1. 仓库地址错误
    echo   2. Token 无效或已过期
    echo   3. 网络问题
    echo.
    echo 解决方法：
    echo   1. 重新生成 Token：https://github.com/settings/tokens
    echo   2. 检查仓库地址是否正确
    echo   3. 如果之前输错密码，执行以下命令清除缓存：
    echo      git credential-manager reject https://github.com
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
