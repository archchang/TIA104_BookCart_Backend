Project Development Strategy: Minimum Viable Product (MVP)

Please cooperate TIA104_BookCart_Frontend project

Deployment Environment
Windows Server 2016 (CPU 2 cores, RAM 16G)

Using Technology:
spring boot (2.7)
spring data jdbc
spring data jpa
spring redis
spring mail
mysql

Deployment
java -Xms8g -Xmx8g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -XX:NewSize=2g -XX:MaxNewSize=4g -XX:CompileThreshold=1000 -jar TIA104_BookCart_Backend-0.0.1-SNAPSHOT.jar
