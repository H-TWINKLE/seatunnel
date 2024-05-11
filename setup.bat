@echo off

setlocal enabledelayedexpansion

title setup seatunnel

:: 获取当前目录
set NOW_PATH=%~dp0
echo now path is %NOW_PATH%

:: 设置工作路径
set WORK_DIR=C:\deployDir\

:: ----------------------------是否强制使用工作路径 1表示强制使用----------------
set FORCE_WORK_DIR="1"

:: ----------------------------是否安装JAVA 1表示安装----------------
set SETUP_JAVA="1"

set REAL_PATH=%NOW_PATH%

IF EXIST %NOW_PATH% echo now path is exist: %NOW_PATH%
IF NOT EXIST %NOW_PATH% echo now path is not exist: %NOW_PATH% && set REAL_PATH=%WORK_DIR%

IF %FORCE_WORK_DIR%=="1" echo force use work space ,real path is exist: %WORK_DIR% && set REAL_PATH=%WORK_DIR%

echo ========= REAL PATH IS %REAL_PATH% =========

IF %SETUP_JAVA%=="1" (
   echo ========= PREPARE TO SETUP JAVA %REAL_PATH%jdk1.8.0_211 =========
   call 7z.exe x jdk1.8.0_211.zip -aoa
   setx JAVA_HOME %REAL_PATH%jdk1.8.0_211 /m
)

set SEATUNNEL_VERSION=2.3.4
set HADOOP_VERSION=3.3.6

:: 复制hoodoop
:: xcopy /s /e /y %NOW_PATH%\hadoop-3.3.6.tar.gz %rootdir%\222

:: 解压当前路径下的文件
call 7z.exe x apache-seatunnel-%SEATUNNEL_VERSION%-bin.tar.gz -aoa
call 7z.exe x apache-seatunnel-%SEATUNNEL_VERSION%-bin.tar -aoa

:: 删除临时文件
del/f/s/q apache-seatunnel-%SEATUNNEL_VERSION%-bin.tar

:: 解压当前路径下的文件
call 7z.exe x hadoop-%HADOOP_VERSION%.tar.gz -aoa
call 7z.exe x hadoop-%HADOOP_VERSION%.tar -aoa

:: 删除临时文件
del/f/s/q hadoop-%HADOOP_VERSION%.tar

:: 解压当前路径下的文件
call 7z.exe x winutils-master.zip -aoa
del/f/s/q %REAL_PATH%hadoop-%HADOOP_VERSION%\bin\
xcopy /s /e /y %REAL_PATH%\winutils-master\hadoop-3.3.5\bin\ %REAL_PATH%\hadoop-%HADOOP_VERSION%\bin\

:: 设置全局环境变量
setx HADOOP_HOME %REAL_PATH%hadoop-%HADOOP_VERSION% /m
setx SEATUNNEL_HOME %REAL_PATH%apache-seatunnel-%SEATUNNEL_VERSION% /m

call 7z.exe x apache-seatunnel-web-1.0.0-SNAPSHOT-%SEATUNNEL_VERSION%.zip -aoa

pause