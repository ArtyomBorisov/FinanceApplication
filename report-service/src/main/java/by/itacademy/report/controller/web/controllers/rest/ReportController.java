package by.itacademy.report.controller.web.controllers.rest;

import by.itacademy.report.model.Report;
import by.itacademy.report.model.api.ReportType;
import by.itacademy.report.service.api.IReportService;
import by.itacademy.report.service.api.MessageError;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.UUID;

@RequestMapping(value = "/report")
@RestController
@Validated
public class ReportController {

    private final IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping(value = "/{type}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable ReportType type,
                       @RequestBody Map<String, Object> params){
        this.reportService.execute(type, params);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Report> get(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                            @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.reportService.get(pageable);
    }

    @GetMapping(value = "/{uuid}/export")
    public ResponseEntity<ByteArrayResource> download(@PathVariable(name = "uuid") UUID id) {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".xlsx");

        byte[] bytes = this.reportService.download(id).toByteArray();
        return new ResponseEntity<>(new ByteArrayResource(bytes), header, HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}/export" , method = RequestMethod.HEAD)
    public ResponseEntity<?> isReady(@PathVariable(name = "uuid") UUID id) {
        if (this.reportService.isReportReady(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
