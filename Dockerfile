FROM openjdk:8-jdk-alpine
ENV AWS_REGION ap-southeast-1
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]