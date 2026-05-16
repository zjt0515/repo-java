FROM eclipse-temurin:8-jdk

WORKDIR /app

ADD target/oj-user-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8102

ENTRYPOINT ["java", "-jar", "/app/oj-user-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]