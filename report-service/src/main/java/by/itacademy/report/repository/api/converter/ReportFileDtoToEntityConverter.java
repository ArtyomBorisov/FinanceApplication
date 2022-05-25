package by.itacademy.report.repository.api.converter;

import by.itacademy.report.model.ReportFile;
import by.itacademy.report.repository.entity.ReportFileEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReportFileDtoToEntityConverter implements Converter<ReportFile, ReportFileEntity> {
    @Override
    public ReportFileEntity convert(ReportFile dto) {
        return ReportFileEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setData(dto.getData().toByteArray())
                .build();
    }
}
