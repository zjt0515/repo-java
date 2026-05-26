FROM eclipse-temurin:8-jdk

WORKDIR /app

ADD target/oj-post-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8105

ENTRYPOINT ["java", "-jar", "/app/oj-post-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
