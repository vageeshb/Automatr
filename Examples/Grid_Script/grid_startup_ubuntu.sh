#!/bin/sh

SELENIUM_VERSION=2.51.0
CHROME_DRIVER_LOC=./chromedriver
HUB_URL=http://localhost:4444/grid/register
gnome-terminal  --window-with-profile=hold_open -e "java -jar selenium-server-standalone-${SELENIUM_VERSION}.jar -role hub"

gnome-terminal --window-with-profile=hold_open -e "java -jar selenium-server-standalone-${SELENIUM_VERSION}.jar -role node -Dwebdriver.chrome.driver=${CHROME_DRIVER_LOC} -hub ${HUB_URL} port 5556 -browser \"browserName=chrome,platform=LINUX\" "

gnome-terminal --window-with-profile=hold_open -e "java -jar selenium-server-standalone-${SELENIUM_VERSION}.jar -role node -hub ${HUB_URL} -port 5557 -browser \"browserName=firefox,platform=LINUX\" "

