@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set ERROR_CODE=0

@REM ==== START VALIDATION ====
if NOT "%JAVA_HOME%"=="" goto OkMvnwJavaHome
for /f "delims=" %%a in ('where java') do set JAVA_EXE=%%a
goto checkJava

:OkMvnwJavaHome
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:checkJava
if exist "%JAVA_EXE%" goto execute
echo ERROR: JAVA_HOME is not set and java could not be found in your PATH.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
goto fail

:execute
set MAVEN_PROJECT_BASEDIR=%~dp0

@REM ==== PROVIDE MAVEN ====
set MAVEN_HOME=%MAVEN_PROJECT_BASEDIR%\.mvn\wrapper\maven-wrapper.jar

if exist "%MAVEN_HOME%" goto runMaven
echo ERROR: Maven Wrapper JAR not found at %MAVEN_HOME%
goto fail

:runMaven
"%JAVA_EXE%" -jar "%MAVEN_HOME%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECT_BASEDIR%" %*

if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /b %ERROR_CODE%