package by.itacademy.mail.service;

import by.itacademy.mail.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.mail.service.api.IMailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class MailService implements IMailService {

    private final RestTemplate restTemplate;
    private final JavaMailSender emailSender;
    private final UserHolder userHolder;

    @Value("${spring.mail.from}")
    private String from;

    @Value("${report_service_url}")
    private String reportUrl;

    public MailService(JavaMailSender emailSender, UserHolder userHolder) {
        this.emailSender = emailSender;
        this.userHolder = userHolder;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void sendReport(UUID id) {
        try {
            String email = userHolder.getLoginFromContext();

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Отчёт " + id);
            helper.setText("");

            String url = reportUrl.replaceFirst("uuid", id.toString());

            HttpHeaders headers = new HttpHeaders();
            String token = JwtTokenUtil.generateAccessToken(userHolder.getUser());
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.HEAD, entity, String.class);

            headers.setContentType(MediaType.TEXT_PLAIN);
            entity = new HttpEntity<>(headers);

            helper.addAttachment(id.toString() + ".xlsx",
                    restTemplate.exchange(url, HttpMethod.GET, entity, ByteArrayResource.class).getBody());

            emailSender.send(message);
        } catch (MailSendException e) {
            throw new IllegalArgumentException("Ваш email невалидный");
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка отправки отчёта");
        } catch (HttpStatusCodeException e) {
            throw new IllegalArgumentException("Отчёт с указанным id недоступен");
        }
    }
}
