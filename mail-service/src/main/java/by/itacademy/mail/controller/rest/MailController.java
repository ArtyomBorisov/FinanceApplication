package by.itacademy.mail.controller.rest;

import by.itacademy.mail.dto.Email;
import by.itacademy.mail.service.impl.MailServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/mail/report/{uuid}")
public class MailController {

    private final MailServiceImpl mailServiceImpl;

    public MailController(MailServiceImpl mailServiceImpl) {
        this.mailServiceImpl = mailServiceImpl;
    }

    @GetMapping
    public ResponseEntity<?> get(
            @PathVariable(name = "uuid") UUID id,
            @RequestBody @Valid Email email) throws MessagingException {

        boolean successful = mailServiceImpl.sendReport(id, email);

        return successful ?
                new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
