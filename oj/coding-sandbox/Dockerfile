FROM eclipse-temurin:8-jdk

WORKDIR /app

ADD target/coding-sandbox-0.0.1-SNAPSHOT.jar .

EXPOSE 8115

ENTRYPOINT ["java", "-jar", "/app/coding-sandbox-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]