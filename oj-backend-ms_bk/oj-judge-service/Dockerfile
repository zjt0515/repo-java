FROM eclipse-temurin:8-jdk

WORKDIR /app

ADD target/oj-judge-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8104

ENTRYPOINT ["java", "-jar", "/app/oj-judge-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]