package by.itacademy.report.controller.web.controllers.rest;

import by.itacademy.report.model.Report;
import by.itacademy.report.model.api.ReportType;
import by.itacademy.report.service.api.IReportService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
public class ReportController {

    private final IReportService reportService;
    private final ApplicationContext context;

    public ReportController(IReportService reportService,
                            ApplicationContext context) {
        this.reportService = reportService;
        this.context = context;
    }

    @PostMapping(value = {"/report/{type}", "/report/{type}/"})
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable ReportType type,
                       @RequestBody Map<String, Object> params){
        this.reportService.execute(type, params);
    }

    @GetMapping(value = {"/report", "/report/"})
    @ResponseBody
    public Page<Report> get(@RequestParam int page,
                            @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.reportService.get(pageable);
    }

    @GetMapping(value = {"/report/{uuid}/export", "/report/{uuid}/export/"})
    public ResponseEntity<ByteArrayResource> download(@PathVariable(name = "uuid") UUID id) {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".xlsx");
        header.setContentType(new MediaType("application", "force-download"));

        byte[] bytes = this.reportService.download(id).toByteArray();
        return new ResponseEntity<>(new ByteArrayResource(bytes), header, HttpStatus.OK);
    }

    @RequestMapping(value = {"/account/{uuid}/export", "account/{uuid}/export/"} , method = RequestMethod.HEAD)
    public ResponseEntity<?> isReady(@PathVariable(name = "uuid") UUID id) {
        if (this.reportService.isReportReady(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
