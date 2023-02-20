package by.itacademy.report.controller.rest;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.dto.Params;
import by.itacademy.report.dto.Report;
import by.itacademy.report.constant.ReportType;
import by.itacademy.report.service.ReportService;
import by.itacademy.report.validation.annotation.CustomValid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.UUID;

@RequestMapping("/report")
@RestController
@Validated
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{type}")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable ReportType type,
                       @RequestBody @CustomValid Params params){
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
        byte[] bytes = reportService.download(id);
        return bytes.length == 0 ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(new ByteArrayResource(bytes), createHeadersWithContent(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}/export" , method = RequestMethod.HEAD)
    public ResponseEntity<?> isReady(@PathVariable(name = "uuid") UUID id) {
        return reportService.isReportReady(id) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private HttpHeaders createHeadersWithContent(UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".xlsx");
        return headers;
    }
}
