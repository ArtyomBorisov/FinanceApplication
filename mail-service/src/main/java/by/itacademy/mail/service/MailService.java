package by.itacademy.mail.service;

import by.itacademy.mail.dto.Email;

import javax.mail.MessagingException;
import java.util.UUID;

public interface MailService {
    boolean sendReport(UUID idReport, Email email) throws MessagingException;
}
