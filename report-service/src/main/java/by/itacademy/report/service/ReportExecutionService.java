package by.itacademy.report.service;

import by.itacademy.report.dto.Params;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ReportExecutionService {
    ByteArrayOutputStream execute(Params params) throws IOException;
}
