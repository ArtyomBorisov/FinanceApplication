package by.itacademy.mail.controller.web.controllers.rest;

import by.itacademy.mail.service.MailService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/mail/report/{uuid}")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping
    public void index(@PathVariable(name = "uuid") UUID id) {
        this.mailService.sendReport(id);
    }
}
