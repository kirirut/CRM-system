# Используем официальный образ OpenJDK 17
FROM openjdk:17-jdk-slim

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY target/srm-system-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=ci"]
