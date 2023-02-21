package by.itacademy.report.service;

import by.itacademy.report.constant.ReportType;
import by.itacademy.report.dto.Params;

import java.util.UUID;

public interface ReportExecutionService {
    void startExecution(ReportType type, Params params, UUID idReport);
}
