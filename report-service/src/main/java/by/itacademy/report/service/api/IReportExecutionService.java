package by.itacademy.report.service.api;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public interface IReportExecutionService {
    ByteArrayOutputStream execute(Map<String, Object> params) throws Exception;
}
