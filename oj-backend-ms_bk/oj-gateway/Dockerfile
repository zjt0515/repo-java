FROM eclipse-temurin:8-jdk

WORKDIR /app

ADD target/oj-gateway-0.0.1-SNAPSHOT.jar .

EXPOSE 8111

ENTRYPOINT ["java", "-jar", "/app/oj-gateway-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]