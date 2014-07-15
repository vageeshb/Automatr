set HERE=%CD%
set PATH=%JAVA_HOME%\jre\bin;%JAVA_HOME%\bin;%PATH%
set SELENIUM_VERSION=2.42.2
set HUB_URL=http://localhost:4444/grid/register
set CHROME_DRIVER_LOC=%HERE%\chromedriver.exe
start java -jar selenium-server-standalone-%SELENIUM_VERSION%.jar -role hub
start java -jar selenium-server-standalone-%SELENIUM_VERSION%.jar -role node -Dwebdriver.chrome.driver=%CHROME_DRIVER_LOC% -hub %HUB_URL% -port 5556 -browser "browserName=chrome,platform=WINDOWS"
start java -jar selenium-server-standalone-%SELENIUM_VERSION%.jar -role node -hub %HUB_URL% -port 5557 -browser "browserName=firefox,platform=WINDOWS"