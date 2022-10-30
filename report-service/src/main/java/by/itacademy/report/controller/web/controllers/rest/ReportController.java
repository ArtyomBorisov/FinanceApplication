package by.itacademy.report.controller.web.controllers.rest;

import by.itacademy.report.dto.Report;
import by.itacademy.report.enums.ReportType;
import by.itacademy.report.service.api.IReportService;
import by.itacademy.report.exception.MessageError;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/report")
@RestController
@Validated
public class ReportController {

    private final IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{type}")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable ReportType type,
                       @RequestBody Map<String, Object> params){
        reportService.execute(type, params);
    }

    @GetMapping
    public Page<Report> get(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                            @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return reportService.get(pageable);
    }

    @GetMapping("/{uuid}/export")
    public ResponseEntity<ByteArrayResource> download(@PathVariable(name = "uuid") UUID id) {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".xlsx");

        byte[] bytes = reportService.download(id).toByteArray();
        return new ResponseEntity<>(new ByteArrayResource(bytes), header, HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}/export" , method = RequestMethod.HEAD)
    public ResponseEntity<?> isReady(@PathVariable(name = "uuid") UUID id) {
        if (reportService.isReportReady(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
