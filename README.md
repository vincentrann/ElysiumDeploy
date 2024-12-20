# Link to source code and SQL scripts
https://github.com/FShalaby/Elysium 
# instructions to download & access
Go to the github repository and use the green Code button to download the entire repository as a zip file
once downloaded zip will contain source code and txt file that has all SQL scripts
# Instructions to run project
Access project online using the link: https://elysiumdeploy-production.up.railway.app
Instructions for localhost machine
## Prerequisites
ensure you have the following installed:
1. JDK: ensure you have java version 18 or higher installed on your computer (check by running command java -version)
2. Maven: ensure you have maven installed (check by running maven -v)
3. ide (vscode recommended): install an ide to view the project and its components more easily 
## Steps to run project locally
1. Extract the zip file to a desired location on your computer
2. (optional) open project in your choice of ide
- vscode: click file > open folder and choose the folder you extract the zip into
3. install dependencies
- run “mvn clean install” in the terminal 
4. local configuration
- in “application.properties” replace to the following to connect to the database locally:
- spring.datasource.url = jdbc:mysql://sql.freedb.tech:3306/freedb_elysium
- spring.datasource.username = freedb_elysium
- spring.datasource.password = ApVPmAPq$8Zp%3V
5. run application
- using terminal run “mvn spring-boot:run”
6. verify application
- go to “http://localhost:8080/” to see application
# Credentials
Navigate to our applications login page (http://localhost:8080/login.html for local usage or https://elysiumdeploy-production.up.railway.app/login.html for online deployment) and enter the following:
- email: admin@gmail.com
- password: adminpass
# Database Note
Our database since it is the free version can only run 1000 queries an hour. If while testing the application does not query data properly (such as products not loading, getting users from admin page, etc), please wait one hour for the max amount of queries to reset and try the application again.
