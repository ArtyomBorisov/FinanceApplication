package by.itacademy.report.service;

import by.itacademy.report.model.Report;
import by.itacademy.report.model.ReportFile;
import by.itacademy.report.model.api.ParamSort;
import by.itacademy.report.model.api.ReportType;
import by.itacademy.report.model.api.Status;
import by.itacademy.report.repository.api.IReportFileRepository;
import by.itacademy.report.repository.api.IReportRepository;
import by.itacademy.report.repository.entity.ReportEntity;
import by.itacademy.report.repository.entity.ReportFileEntity;
import by.itacademy.report.service.api.Errors;
import by.itacademy.report.service.api.IReportExecutionService;
import by.itacademy.report.service.api.IReportService;
import by.itacademy.report.service.api.ValidationException;
import by.itacademy.report.service.execution.ReportBalanceExecutionService;
import by.itacademy.report.service.execution.ReportOperationSortByParamExecutionService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService implements IReportService {

    private IReportExecutionService reportExecutionService;
    private final IReportFileRepository reportFileRepository;
    private final IReportRepository reportRepository;
    private final ConversionService conversionService;
    private final ApplicationContext context;

    public ReportService(IReportFileRepository reportFileRepository,
                         IReportRepository reportRepository,
                         ConversionService conversionService,
                         ApplicationContext context) {
        this.reportFileRepository = reportFileRepository;
        this.reportRepository = reportRepository;
        this.conversionService = conversionService;
        this.context = context;
    }

    @Transactional
    @Override
    public void execute(ReportType type, Map<String, Object> params) {
        switch (type) {
            case BALANCE:
                this.reportExecutionService = context.getBean(ReportBalanceExecutionService.class);
                break;
            case BY_DATE:
                params.put("type", ParamSort.BY_DATE_AND_TIME);
                this.reportExecutionService = context.getBean(ReportOperationSortByParamExecutionService.class);
                break;
            case BY_CATEGORY:
                params.put("type", ParamSort.BY_CATEGORY_NAME);
                this.reportExecutionService = context.getBean(ReportOperationSortByParamExecutionService.class);
                break;
            default:
                throw new ValidationException("Нет реализации такого отчёта");
        }

        ByteArrayOutputStream data = this.reportExecutionService.execute(params);

        UUID id = UUID.randomUUID();
        LocalDateTime timeNow = LocalDateTime.now();

        Report report = Report.Builder.createBuilder()
                .setId(id)
                .setDtCreate(timeNow)
                .setDtUpdate(timeNow)
                .setStatus(Status.DONE)
                .setType(type)
                .setDescription("Описание отчёта")
                .setParams(params)
                .build();

        try {
            this.reportRepository.save(this.conversionService.convert(report, ReportEntity.class));

            this.reportFileRepository.save(this.conversionService.convert(new ReportFile(id, data), ReportFileEntity.class));
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.name(), e);
        }
    }

    @Override
    public Page<Report> get(Pageable pageable) {
        Page<ReportEntity> entities;

        try {
            entities = this.reportRepository.findByOrderByDtCreateAsc(pageable);
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.name(), e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Report.class))
                .collect(Collectors.toList()));
    }

    @Override
    public ByteArrayOutputStream download(UUID id) {
        if (isReportReady(id)) {
            ReportFileEntity fileEntity;

            try {
                fileEntity = this.reportFileRepository.findById(id).get();
            } catch (Exception e) {
                throw new RuntimeException(Errors.SQL_ERROR.name(), e);
            }

            return this.conversionService.convert(fileEntity,
                    ReportFile.class).getData();
        } else {
            throw new ValidationException("Передан неверный id");
        }
    }

    @Override
    public boolean isReportReady(UUID id) {
        try {
            return this.reportRepository.findByIdAndStatus(id, Status.DONE.toString()).isPresent();
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.name(), e);
        }
    }
}
