package by.itacademy.report.converter;

import by.itacademy.report.dto.Report;
import by.itacademy.report.dao.entity.ReportEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReportDtoToEntityConverter implements Converter<Report, ReportEntity> {
    @Override
    public ReportEntity convert(Report dto) {
        return ReportEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setDtCreate(dto.getDtCreate())
                .setDtUpdate(dto.getDtUpdate())
                .setStatus(dto.getStatus() == null ? null : dto.getStatus().toString())
                .setType(dto.getType() == null ? null : dto.getType().toString())
                .setDescription(dto.getDescription())
                .setParams(dto.getParams())
                .setUser(dto.getUser())
                .build();
    }
}
