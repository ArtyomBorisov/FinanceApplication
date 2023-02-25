FROM swaggerapi/swagger-ui:latest
MAINTAINER ITAcademy

ADD /account-scheduler-service/spec/account-scheduler-service.yaml /usr/share/nginx/html/doc/account-scheduler-service.yaml
ADD /account-service/spec/account-service.yaml /usr/share/nginx/html/doc/account-service.yaml
ADD /classifier-service/spec/classifier-service.yaml /usr/share/nginx/html/doc/classifier-service.yaml
ADD /mail-scheduler-service/spec/mail-scheduler-service.yaml /usr/share/nginx/html/doc/mail-scheduler-service.yaml
ADD /mail-service/spec/mail-service.yaml /usr/share/nginx/html/doc/mail-service.yaml
ADD /report-service/spec/report-service.yaml /usr/share/nginx/html/doc/report-service.yaml


ENV URLS="[{ url: 'doc/account-scheduler-service.yaml', name: 'AccountScheduling'}, { url: 'doc/account-service.yaml', name: 'Account'},{ url: 'doc/classifier-service.yaml', name: 'Classifier'},{ url: 'doc/mail-scheduler-service.yaml', name: 'MailScheduling'},{ url: 'doc/mail-service.yaml', name: 'Mail'},{ url: 'doc/report-service.yaml', name: 'Report'}]"
ENV URLS_PRIMARY_NAME="Account"