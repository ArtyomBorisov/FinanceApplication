FROM adoptopenjdk/openjdk11:alpine-jre
MAINTAINER ITAcademy

ADD ./target/classifier-service.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/classifier-service.jar"]

EXPOSE 8080 8089