package by.itacademy.mail.service.api;

import javax.mail.internet.AddressException;
import java.util.UUID;

public interface IMailService {
    void sendReport(UUID idReport) throws AddressException;
}
