package by.itacademy.report.converter;

import by.itacademy.report.dto.ReportFile;
import by.itacademy.report.dao.entity.ReportFileEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReportFileDtoToEntityConverter implements Converter<ReportFile, ReportFileEntity> {
    @Override
    public ReportFileEntity convert(ReportFile dto) {
        return ReportFileEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setData(dto.getData().toByteArray())
                .setUser(dto.getUser())
                .build();
    }
}
