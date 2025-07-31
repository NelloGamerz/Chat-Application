FROM openjdk:17-jdk-slim
EXPOSE 8080
ADD target/webchat-userrepo.jar webchat-userrepo.jar
ENTRYPOINT ["java", "-jar","/webchat-userrepo.jar"]