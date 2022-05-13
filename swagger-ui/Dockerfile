FROM swaggerapi/swagger-ui:latest
MAINTAINER ITAcademy

ADD /account-scheduler-service/spec/account-scheduler-service.yaml /doc/account-scheduler-service.yaml
ADD /account-service/spec/account-service.yaml /doc/account-service.yaml
ADD /classifier-service/spec/classifier-service.yaml /doc/classifier-service.yaml
ADD /report-service/spec/report-service.yaml /doc/report-service.yaml

ENV URLS="[{ url: 'doc/account-scheduler-service.yaml', name: 'AccountScheduling'}, { url: 'doc/account-service.yaml', name: 'Account'},{ url: 'doc/classifier-service.yaml', name: 'Classifier'},{ url: 'doc/report-service.yaml', name: 'Report'},]"
ENV URLS_PRIMARY_NAME="Account"