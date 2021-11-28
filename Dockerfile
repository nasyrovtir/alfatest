FROM openjdk:11.0.7-jdk-slim

ARG JAR_FILE=build/libs/alfatest-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

CMD ["java","-jar","/app.jar"]