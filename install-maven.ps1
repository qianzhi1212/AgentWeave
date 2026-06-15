# 一键安装 Maven 的脚本
# 用法: 右键此文件 -> 使用 PowerShell 运行
# 或在 PowerShell 中: .\install-maven.ps1

$ErrorActionPreference = "Stop"

$mavenVersion = "3.9.6"
$installDir = "C:\maven"
$binDir = Join-Path $installDir "bin"
$zipUrl = "https://repo1.maven.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"
$zipFile = Join-Path $env:TEMP "maven.zip"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Maven $mavenVersion 一键安装" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Download
if (Test-Path $installDir) {
    Write-Host "[跳过] Maven 已安装在 $installDir" -ForegroundColor Yellow
} else {
    Write-Host "[1/3] 正在下载 Maven $mavenVersion ..." -ForegroundColor Green
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $zipUrl -OutFile $zipFile -UseBasicParsing
    Write-Host "       下载完成！" -ForegroundColor Green

    # Step 2: Extract
    Write-Host "[2/3] 正在解压到 $installDir ..." -ForegroundColor Green
    Expand-Archive -Path $zipFile -DestinationPath $env:TEMP -Force
    $extractedDir = Join-Path $env:TEMP "apache-maven-$mavenVersion"
    Move-Item -Path $extractedDir -Destination $installDir -Force
    Remove-Item $zipFile -Force
    Write-Host "       解压完成！" -ForegroundColor Green
}

# Step 3: Set environment variables
Write-Host "[3/3] 正在配置环境变量 ..." -ForegroundColor Green

# Set MAVEN_HOME
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", $installDir, "User")

# Add to PATH
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", "User")
if ($currentPath -notlike "*$binDir*") {
    $newPath = $currentPath + ";" + $binDir
    [System.Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    $env:Path = $env:Path + ";" + $binDir
}

Write-Host "       环境变量已设置！" -ForegroundColor Green
Write-Host ""

# Verify
$mvnCmd = Join-Path $binDir "mvn.cmd"
if (Test-Path $mvnCmd) {
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  安装成功！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "请关闭当前窗口，重新打开 PowerShell，" -ForegroundColor Yellow
    Write-Host "然后运行: mvn -v" -ForegroundColor Yellow
} else {
    Write-Host "安装可能有问题，请检查 $installDir 目录" -ForegroundColor Red
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')