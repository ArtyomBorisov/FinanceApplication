package by.itacademy.report.utils.converter;

import by.itacademy.report.dto.ReportFile;
import by.itacademy.report.repository.entity.ReportFileEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class ReportFileEntityToDtoConverter implements Converter<ReportFileEntity, ReportFile> {
    @Override
    public ReportFile convert(ReportFileEntity entity) {
        byte[] data = entity.getData();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        outputStream.write(data, 0, data.length);
        return new ReportFile(entity.getId(), outputStream, entity.getUser());
    }
}
