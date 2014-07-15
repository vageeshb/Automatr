@ECHO OFF
set HERE=%CD%
set PATH=%JAVA_HOME%\jre\bin;%JAVA_HOME%\bin;%PATH%
set SELENIUM_VERSION=2.39.0
set HUB_URL=http://localhost:4444/grid/register
set CHROME_DRIVER_LOC=%HERE%\chromedriver.exe
REM - THE BELOW LINE GIVES THE USER 3 CHOICES (DEFINED AFTER /C:)
CHOICE /N /C:12 /M "1 - Set up Firefox Node, or 2 - Set up Chrome Node, SELECT?" %1
REM - THE NEXT THREE LINES ARE DIRECTING USER DEPENDING UPON INPUT
IF ERRORLEVEL ==2 GOTO TWO
IF ERRORLEVEL ==1 GOTO ONE
GOTO END
:ONE
start java -jar selenium-server-standalone-%SELENIUM_VERSION%.jar -role node -hub %HUB_URL% -port 5557 -browser "browserName=firefox,platform=WINDOWS"
GOTO END
:TWO
start java -jar selenium-server-standalone-%SELENIUM_VERSION%.jar -role node -hub %HUB_URL% -Dwebdriver.chrome.driver=%CHROME_DRIVER_LOC% -port 5556 -browser "browserName=chrome,platform=WINDOWS"
:END