package by.itacademy.report.service;

import by.itacademy.report.model.Report;
import by.itacademy.report.model.ReportFile;
import by.itacademy.report.model.api.ReportType;
import by.itacademy.report.model.api.Status;
import by.itacademy.report.repository.api.IReportFileRepository;
import by.itacademy.report.repository.api.IReportRepository;
import by.itacademy.report.repository.entity.ReportEntity;
import by.itacademy.report.repository.entity.ReportFileEntity;
import by.itacademy.report.service.api.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final UserHolder userHolder;

    public ReportService(IReportFileRepository reportFileRepository,
                         IReportRepository reportRepository,
                         ConversionService conversionService,
                         ApplicationContext context,
                         UserHolder userHolder) {
        this.reportFileRepository = reportFileRepository;
        this.reportRepository = reportRepository;
        this.conversionService = conversionService;
        this.context = context;
        this.userHolder = userHolder;
    }

    @Transactional
    @Override
    public void execute(ReportType type, Map<String, Object> params) {
        String login = userHolder.getUser().getUsername();
        String description;
        String pattern = "dd.MM.yyyy";

        switch (type) {
            case BALANCE:
                this.reportExecutionService = context.getBean(ReportBalanceExecutionService.class);
                description = "Отчёт по балансам, запрос от ";
                break;
            case BY_DATE:
                this.reportExecutionService = context.getBean(ReportOperationSortByParamExecutionService.class);
                description = "Отчёт по операциям в разрезе дат, запрос от ";
                break;
            case BY_CATEGORY:
                this.reportExecutionService = context.getBean(ReportOperationSortByParamExecutionService.class);
                description = "Отчёт по операциям в разрезе категорий, запрос от ";
                break;
            default:
                throw new RuntimeException("Нет реализации такого отчёта");
        }

        UUID id = UUID.randomUUID();
        LocalDateTime timeNow = LocalDateTime.now();

        Report report = Report.Builder.createBuilder()
                .setId(id)
                .setDtCreate(timeNow)
                .setDtUpdate(timeNow)
                .setStatus(Status.LOADED)
                .setType(type)
                .setDescription(description + LocalDate.now().format(DateTimeFormatter.ofPattern(pattern)))
                .setParams(params)
                .setUser(login)
                .build();

        params.put("type", type.toString());
        ByteArrayOutputStream data = null;

        try {
            data = this.reportExecutionService.execute(params);
            report.setStatus(Status.DONE);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            report.setStatus(Status.ERROR);
        }

        params.remove("type");

        try {
            this.reportRepository.save(this.conversionService.convert(report, ReportEntity.class));

            if (data != null) {
                this.reportFileRepository.save(this.conversionService.convert(
                        new ReportFile(id, data, login),
                        ReportFileEntity.class));
            }
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }

    @Override
    public Page<Report> get(Pageable pageable) {
        String login = this.userHolder.getLoginFromContext();

        Page<ReportEntity> entities;

        try {
            entities = this.reportRepository.findByUserOrderByDtCreateAsc(login, pageable);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Report.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public ByteArrayOutputStream download(UUID id) {
        String login = this.userHolder.getLoginFromContext();

        if (isReportReady(id)) {
            ReportFileEntity fileEntity;

            try {
                fileEntity = this.reportFileRepository.findByUserAndId(login, id).get();
            } catch (Exception e) {
                throw new RuntimeException(MessageError.SQL_ERROR, e);
            }

            return this.conversionService.convert(fileEntity,
                    ReportFile.class).getData();
        } else {
            throw new ValidationException(new ValidationError("id", MessageError.ID_NOT_EXIST));
        }
    }

    @Override
    public boolean isReportReady(UUID id) {
        String login = this.userHolder.getLoginFromContext();

        try {
            return this.reportRepository.findByUserAndIdAndStatus(login, id, Status.DONE.toString()).isPresent();
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }
}
