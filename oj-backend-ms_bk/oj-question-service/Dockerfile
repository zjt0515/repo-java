FROM eclipse-temurin:8-jdk

WORKDIR /app

ADD target/oj-question-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8103

ENTRYPOINT ["java", "-jar", "/app/oj-question-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]