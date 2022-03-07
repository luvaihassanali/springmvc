Networking quest is working (for all human players).

*See GUIShots folder for screenshots of gameplay.*

To run in eclipse:

1. import project folder from repository into eclipse
2. create new run configuration for project workspace with maven and 'spring-boot: run' as parameter
3. server will start and players can now join by connecting to *host-ip-adress*:8080
(or localhost:8080 can connect 4 players locally in different browser windows)

To run with java in desktop environment (tested in windows/linux)

1. navigate to /team40-quest/target in repository
2. run command "java -jar spring-quest-0.0.1-SNAPSHOT.war"
3. server will open in terminal and players will now be able to connect.

Note: I have already compiled the snapshot for you, if you want to create a new one:
1. create new run configuration for project workspace with maven and 'clean package' as paramater
2. snapshot will be overrided and previous will be renamed with suffix .old
