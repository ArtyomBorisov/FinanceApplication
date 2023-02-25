package by.itacademy.mail.scheduler.controller.rest;

import by.itacademy.mail.scheduler.dto.MonthlyReport;
import by.itacademy.mail.scheduler.service.MonthlyReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/scheduler/mail/report")
public class MailSchedulerController {

    private final MonthlyReportService service;

    public MailSchedulerController(MonthlyReportService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> get() {
        MonthlyReport report = service.get();
        return report != null ?
                new ResponseEntity<>(report, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid MonthlyReport monthlyReport) {
        service.add(monthlyReport);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        service.delete();
    }
}
