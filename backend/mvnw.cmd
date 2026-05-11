@REM
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM ------------------
@REM   JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM -----------------
@REM   M2_HOME - location of maven2's installed home dir
@REM   MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM   MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM   MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off
@REM set title of command window
title %0
@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%"=="on"  echo %MAVEN_BATCH_ECHO%

@REM set %HOME% to the root of the Maven installation
if "%HOME%"=="" set HOME=%MAVEN_HOME%

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%"=="" goto skipRcPre
@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%HOME%\mavenrc_pre.bat" call "%HOME%\mavenrc_pre.bat"
if exist "%HOME%\mavenrc_pre.cmd" call "%HOME%\mavenrc_pre.cmd"
:skipRcPre

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%"=="" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" goto endReadAdditionalConfig

@setlocal EnableExtensions EnableDelayedExpansion
for /F "usebackq delims=" %%a in ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%a
@endlocal & set JVM_CONFIG_MAVEN_PROPS=%JVM_CONFIG_MAVEN_PROPS%

:endReadAdditionalConfig

SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM -- 4.3.1: ensure the --add-modules=java.se is added for Java 9+ compatibility
set WRAPPER_JVM_OPTS=
for %%i in (%JVM_CONFIG_MAVEN_PROPS%) do (
  if /i "%%i"=="--add-modules" set WRAPPER_JVM_OPTS=%%i
)

if not "%WRAPPER_JVM_OPTS%"=="" goto skipAddModules
if "%JAVA_VERSION%"=="9" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="10" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="11" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="12" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="13" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="14" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="15" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="16" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="17" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="18" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="19" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="20" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="21" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="22" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="23" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="24" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="25" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="26" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="27" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="28" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="29" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="30" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="31" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="32" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="33" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="34" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="35" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="36" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="37" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="38" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="39" set WRAPPER_JVM_OPTS=--add-modules=java.se
if "%JAVA_VERSION%"=="40" set WRAPPER_JVM_OPTS=--add-modules=java.se

:skipAddModules

@REM Start MAVEN2
%MAVEN_JAVA_EXE% %WRAPPER_JVM_OPTS% %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath %WRAPPER_JAR%;%MAVEN_HOME%\boot\plexus-classworlds-2.6.0.jar "-Dclassworlds.conf=%MAVEN_HOME%\bin\m2.conf" "-Dmaven.home=%MAVEN_HOME%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %MAVEN_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@REM check for post script, once with legacy .bat ending and once with .cmd ending
if exist "%HOME%\mavenrc_post.bat" call "%HOME%\mavenrc_post.bat"
if exist "%HOME%\mavenrc_post.cmd" call "%HOME%\mavenrc_post.cmd"
:skipRcPost

@REM pause the script if MAVEN_BATCH_PAUSE is set to 'on'
if "%MAVEN_BATCH_PAUSE%"=="on" pause

if "%MAVEN_ERROR_CODE%"=="1" exit /b 1
exit /b %ERROR_CODE%
