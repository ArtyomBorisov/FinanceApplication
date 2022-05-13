FROM adoptopenjdk/openjdk11:alpine-jre
MAINTAINER ITAcademy

ADD ./target/account-scheduler-service.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/account-scheduler-service.jar"]

EXPOSE 8080 8089