FROM openjdk:11-jre-slim

ARG JAR_FILE=./build/libs/*-SNAPSHOT.jar

COPY ${JAR_FILE} ataste.jar

ENTRYPOINT ["java","-jar","/ataste.jar"]

