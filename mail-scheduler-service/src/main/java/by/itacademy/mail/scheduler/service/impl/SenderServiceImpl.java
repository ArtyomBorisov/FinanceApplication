package by.itacademy.mail.scheduler.service.impl;

import by.itacademy.mail.scheduler.constant.ReportType;
import by.itacademy.mail.scheduler.dto.LoginReportNameEmailDto;
import by.itacademy.mail.scheduler.service.SenderService;
import by.itacademy.mail.scheduler.utils.Generator;
import by.itacademy.mail.scheduler.utils.impl.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static by.itacademy.mail.scheduler.constant.MessageError.*;

@Service
public class SenderServiceImpl implements SenderService {

    private final RestTemplate restTemplate;
    private final Generator generator;
    private final String reportUrl;
    private final String mailUrl;

    private final Logger logger = LogManager.getLogger(SenderServiceImpl.class);

    public SenderServiceImpl(RestTemplate restTemplate,
                             Generator generator,
                             @Value("urls.report") String reportUrl,
                             @Value("urls.mail") String mailUrl) {
        this.restTemplate = restTemplate;
        this.generator = generator;
        this.reportUrl = reportUrl;
        this.mailUrl = mailUrl;
    }

    @Override
    public String prepareReport(String login, ReportType reportType) {
        final String urlWithType = reportUrl + "/" + reportType;
        HttpEntity<Params> request = new HttpEntity<>(createParams(), getHeaders(login));
        try {
            return restTemplate.postForObject(urlWithType, request, String.class);
        } catch (Exception e) {
            logger.error("{}: {}; {}", REPORT_MAKING_EXCEPTION, e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    @Async
    @Override
    public void send(LoginReportNameEmailDto dto) {
        String email = dto.getEmail();
        String login = dto.getLogin();
        String reportName = dto.getReportName();

        if (reportName == null) {
            logger.error("{}: {}", REPORT_SENDING_EXCEPTION, REPORT_NOT_EXIST);
            return;
        }

        final String urlWithName = mailUrl + "/" + reportName;
        HttpEntity<Email> request = new HttpEntity<>(new Email(email), getHeaders(login));
        try {
            restTemplate.postForObject(urlWithName, request, String.class);
        } catch (Exception e) {
            logger.error("{}: {}; {}", REPORT_SENDING_EXCEPTION, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private Params createParams() {
        LocalDate dateFromRequiredMonth = generator.dateNow().minusMonths(1);
        int lastDay = dateFromRequiredMonth.getMonth().length(dateFromRequiredMonth.isLeapYear());
        LocalDate from = dateFromRequiredMonth.withDayOfMonth(1);
        LocalDate to = dateFromRequiredMonth.withDayOfMonth(lastDay);
        return new Params(from, to);
    }

    private HttpHeaders getHeaders(String login) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(login);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    private static class Email {
        private final String address;

        public Email(String address) {
            this.address = address;
        }
    }

    private static class Params {
        private final LocalDate from;
        private final LocalDate to;

        public Params(LocalDate from, LocalDate to) {
            this.from = from;
            this.to = to;
        }
    }
}
