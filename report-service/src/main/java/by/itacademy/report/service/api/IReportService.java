package by.itacademy.report.service.api;

import by.itacademy.report.dto.Report;
import by.itacademy.report.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

public interface IReportService {
    void execute(ReportType type, Map<String, Object> params);
    Page<Report> get(Pageable pageable);
    ByteArrayOutputStream download(UUID id);
    boolean isReportReady(UUID id);
}
