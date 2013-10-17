@echo off & SETLOCAL

SET _prompt=%1

REM VBS script mit einem Echo Inputbox statement:
ECHO Wscript.Echo Inputbox("Bitte geben Sie den Namen f�r den Log-Ordner ein: %_prompt%","Name")>%TEMP%\~input.vbs

REM vbScript ausf�hren und output speichern
FOR /f "delims=/" %%G IN ('cscript //nologo %TEMP%\~input.vbs') DO set _string=%%G

REM VBS-Datei l�schen und Input speichern
DEL %TEMP%\~input.vbs
ENDLOCAL & SET _input=%_string%
SET LogOrdner=%_input%

SET _prompt=%1

REM VBS script mit einem Echo Inputbox statement:
ECHO Wscript.Echo Inputbox("Bitte geben Sie den Pfad zum Ordner mit den zu validierenden Dateien oder die einzelne Datei an:%_prompt%","Pfad", "C:\TEMP\TIFF")>%TEMP%\~input.vbs

REM vbScript ausf�hren und output speichern
FOR /f "delims=/" %%G IN ('cscript //nologo %TEMP%\~input.vbs') DO set _string=%%G

REM VBS-Datei l�schen und Input speichern
DEL %TEMP%\~input.vbs
ENDLOCAL & SET _input=%_string%
SET DATEIEN=%_input%

@echo off
REM Ordner "logs" anlegen
MKDIR logs\"%LogOrdner%"

ECHO Validierungsdatum: %date% >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO Start der Validierung: %time% >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO. >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO =========================== T I F F - V A L ===========================  >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO. >> logs\"%LogOrdner%"\_KOST-Val-Summary.log


REM setzen der variablen f�r die zusammenfassung der errorlevel
set g=0
REM g=gut=Datei Valid -> errorlevel=0
set h=0
REM h=hilfe=Datei-Aufruf-Fehler -> errorlevel=1
set i=0
REM i=invalid=Datei Invalid -> errorlevel=2

ECHO.
ECHO Aufbau KOST-Val Befehl: 
ECHO Java-Pfad  -jar  kostval.jar-Pfad  %DATEIEN%  logs\%LogOrdner%
ECHO.
ECHO ============================== S T A R T ==============================   
ECHO.
IF exist "%DATEIEN%\" (
    REM It's a directory
    REM --- FOR Schleife f�r die Validierung aller TIFF- & SIARD-Dateien inkl.Unterordner --- 
    FOR /R "%DATEIEN%" %%J In (*.tif *.siard) DO (
    SET Datei=%%J
    ECHO.
    Drittapplikationen\jre6\bin\java.exe -jar KOST-Val\kostval.jar "%%J" "logs\%LogOrdner%"
    CALL :sub_ord "Datei" "LogOrdner"
    ECHO.
    ECHO --------------------
    )
) ELSE (
    REM It's a file
    REM --- Fals eine Datei eingeben wurde
    Drittapplikationen\jre6\bin\java.exe -jar KOST-Val\kostval.jar "%DATEIEN%" "logs\%LogOrdner%"
    SET Datei=%DATEIEN%
    CALL :sub_ord "Datei" "LogOrdner"
)
ECHO.
ECHO. >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO ==================== Z U S A M M E N F A S S U N G ====================  >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO. >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO          Valide = %g%    Invalide = %i%   Fehler im Aufruf = %h% >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO. >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO ================================ E N D ================================  >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO. >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO Validierung beendet: %time% >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO. >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
ECHO ==================== Z U S A M M E N F A S S U N G ====================  
ECHO.
ECHO           Valide = %g%    Invalide = %i%   Fehler im Aufruf = %h%
ECHO.
ECHO ================================ E N D ================================  
ECHO.
PAUSE
EXIT /B 

REM muss via "CALL :sub_ord" erfolgen weil dies in FOR nicht funktioniert
:sub_ord
 ECHO return code %errorlevel%
 IF %errorlevel% == 0 (
  set /a g+=1
  ECHO Valide: %Datei% >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
 ) ELSE (
  IF %errorlevel% == 2 (
   set /a i+=1
   ECHO INVALIDE: %Datei% >> logs\"%LogOrdner%"\_KOST-Val-Summary.log
  ) ELSE (
   set /a h+=1
  )
 )
 GOTO :eof