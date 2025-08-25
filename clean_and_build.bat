@echo off
echo ========================================
echo LIMPIANDO Y RECONSTRUYENDO PROYECTO
echo ========================================

echo.
echo 1. Limpiando proyecto...
call gradlew clean

echo.
echo 2. Limpiando cache de Gradle...
call gradlew cleanBuildCache

echo.
echo 3. Reconstruyendo proyecto...
call gradlew build

echo.
echo 4. Instalando en dispositivo/emulador...
call gradlew installDebug

echo.
echo ========================================
echo PROCESO COMPLETADO
echo ========================================
pause
