FROM adoptopenjdk/openjdk11:alpine-jre
MAINTAINER ITAcademy

ADD ./target/mail-service.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/mail-service.jar"]

EXPOSE 8080 8089