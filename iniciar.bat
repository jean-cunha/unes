@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem Compila tudo com o mesmo JDK que vai executar (evita erro de versao) e inicia o sistema.
set "LISTA=%TEMP%\unes_fontes.txt"
if exist "%LISTA%" del "%LISTA%"
for /r "src" %%f in (*.java) do (
  set "p=%%f"
  echo "!p:\=/!">>"%LISTA%"
)

if exist out rmdir /s /q out
mkdir out
javac -encoding UTF-8 -d out -cp "lib/mysql-connector-j-9.7.0.jar" @"%LISTA%"
if errorlevel 1 (
  echo.
  echo Falha ao compilar. Confira se o JDK esta instalado ^(javac -version^).
  pause
  exit /b 1
)

java -cp "out;lib/mysql-connector-j-9.7.0.jar" br.com.unes.Main
if errorlevel 1 pause
