package by.itacademy.mail.service.impl;

import by.itacademy.mail.constant.MessageError;
import by.itacademy.mail.dto.Email;
import by.itacademy.mail.exception.ServerException;
import by.itacademy.mail.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class MailServiceImpl implements MailService {

    private final RestTemplate restTemplate;
    private final JavaMailSender emailSender;
    private final String from;
    private final String reportUrl;

    public MailServiceImpl(RestTemplate restTemplate,
                           JavaMailSender emailSender,
                           @Value("${spring.mail.from}") String from,
                           @Value("${urls.report}") String reportUrl) {
        this.restTemplate = restTemplate;
        this.emailSender = emailSender;
        this.from = from;
        this.reportUrl = reportUrl;
    }

    @Override
    public boolean sendReport(UUID id, Email email) throws MessagingException {
        String address = email.getAddress();
        String url = String.format(reportUrl, id.toString());

        if(!isReportExist(url)){
            return false;
        }

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(address);
        helper.setSubject("Отчёт " + id);
        helper.setText("");

        ByteArrayResource report = getReport(url);

        helper.addAttachment(id + ".xlsx", report);
        emailSender.send(message);
        return true;
    }

    private boolean isReportExist(String url) {
        HttpEntity<?> entity = getHttpEntity();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.HEAD, entity, String.class);

        return response.getStatusCode().compareTo(HttpStatus.NO_CONTENT) != 0;
    }

    private ByteArrayResource getReport(String url) {
        HttpEntity<?> entity = getHttpEntity(MediaType.TEXT_PLAIN);
        ResponseEntity<ByteArrayResource> responseWithData = restTemplate
                .exchange(url, HttpMethod.GET, entity, ByteArrayResource.class);

        ByteArrayResource resource = responseWithData.getBody();
        if (resource == null) {
            throw new ServerException(MessageError.REPORT_GETTING_EXCEPTION);
        }

        return resource;
    }

    private HttpEntity<?> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?> getHttpEntity(MediaType type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        return new HttpEntity<>(headers);
    }
}
