## SELENIUM GRID SETUP EXAMPLE ##

The framework requires a grid server to be launched in order to perform execution. This document outlines the procedure to setup up Selenium grid to perform test script execution. 

### SETUP STEPS ###
* Download and place the '[Selenium Server Standalone v2.51.0](http://selenium-release.storage.googleapis.com/2.51/selenium-server-standalone-2.51.0.jar)' jar file in this folder
* Download and place the '[Chrome Driver](http://chromedriver.storage.googleapis.com/index.html?path=2.10/)' executable file in this folder
* Run the batch/shell file (depending on your system OS) for launching the grid server with 1 Firefox and 1 Chrome instance
* (Optional) In case an instance shuts down due to error, relaunch single node using 'individual-node-launch' batch file.

### Advanced Instructions ###
You can edit the batch file as per your need, the framework just expects a grid server and browser instances to be running.