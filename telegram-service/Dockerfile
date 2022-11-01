FROM adoptopenjdk/openjdk11:alpine-jre
MAINTAINER ITAcademy

ADD ./target/telegram-service.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/telegram-service.jar"]

EXPOSE 8080 8089