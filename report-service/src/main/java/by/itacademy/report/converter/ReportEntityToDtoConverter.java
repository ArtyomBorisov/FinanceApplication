package by.itacademy.report.converter;

import by.itacademy.report.dto.Report;
import by.itacademy.report.constant.ReportType;
import by.itacademy.report.constant.Status;
import by.itacademy.report.dao.entity.ReportEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReportEntityToDtoConverter implements Converter<ReportEntity, Report> {
    @Override
    public Report convert(ReportEntity entity) {
        return Report.Builder.createBuilder()
                .setId(entity.getId())
                .setDtCreate(entity.getDtCreate())
                .setDtUpdate(entity.getDtUpdate())
                .setStatus(Status.valueOf(entity.getStatus()))
                .setType(ReportType.valueOf(entity.getType()))
                .setDescription(entity.getDescription())
                .setParams(entity.getParams())
                .setUser(entity.getUser())
                .build();
    }
}
