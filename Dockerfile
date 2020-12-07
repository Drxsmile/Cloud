FROM openjdk:8-jdk-alpine
ENV AWS_ACCESS_KEY_ID $AWS_ACCESS_KEY_ID
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]