FROM openjdk:17-jdk-slim
EXPOSE 8761
ADD target/web-chat-service-registry.jar web-chat-service-registry.jar
ENTRYPOINT ["java", "-jar", "/web-chat-service-registry.jar"]