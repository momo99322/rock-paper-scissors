FROM openjdk:21
COPY build/libs/rock-paper-scissors-1.0-SNAPSHOT.jar /app.jar
COPY src/main/resources/* resources/
ENTRYPOINT ["java","-jar","/app.jar"]