package by.itacademy.report.service;

import by.itacademy.report.dto.Params;

import java.io.IOException;

public interface ReportExecutionService {
    byte[] execute(Params params) throws IOException;
}
