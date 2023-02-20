package by.itacademy.report.service;

import by.itacademy.report.dto.Params;
import by.itacademy.report.dto.Report;
import by.itacademy.report.constant.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReportService {
    void execute(ReportType type, Params params);
    Page<Report> get(Pageable pageable);
    byte[] download(UUID id);
    boolean isReportReady(UUID id);
}
