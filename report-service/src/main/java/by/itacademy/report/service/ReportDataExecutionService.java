package by.itacademy.report.service;

import by.itacademy.report.dto.Params;

import java.io.IOException;

public interface ReportDataExecutionService {
    byte[] execute(Params params) throws IOException;
}
