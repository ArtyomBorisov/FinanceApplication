package by.itacademy.report.converter;

import by.itacademy.report.constant.ReportType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomReportTypeConverter implements Converter<String, ReportType> {
    @Override
    public ReportType convert(String value) {
        return ReportType.valueOf(value.toUpperCase());
    }
}
