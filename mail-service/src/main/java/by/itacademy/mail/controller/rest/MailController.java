package by.itacademy.mail.controller.rest;

import by.itacademy.mail.dto.Email;
import by.itacademy.mail.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/mail/report/{uuid}")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping
    public ResponseEntity<?> get(@PathVariable(name = "uuid") UUID id,
                                 @RequestBody @Valid Email email) throws MessagingException {

        boolean successful = mailService.sendReport(id, email);

        return successful ?
                new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
