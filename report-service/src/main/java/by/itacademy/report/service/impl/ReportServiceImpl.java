package by.itacademy.report.service.impl;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.dto.Params;
import by.itacademy.report.dto.Report;
import by.itacademy.report.dto.ReportFile;
import by.itacademy.report.constant.ReportType;
import by.itacademy.report.constant.Status;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.dao.ReportFileRepository;
import by.itacademy.report.dao.ReportRepository;
import by.itacademy.report.dao.entity.ReportEntity;
import by.itacademy.report.dao.entity.ReportFileEntity;
import by.itacademy.report.service.ReportService;
import by.itacademy.report.service.ReportExecutionService;
import by.itacademy.report.service.UserHolder;
import by.itacademy.report.utils.Generator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportFileRepository reportFileRepository;
    private final ReportRepository reportRepository;
    private final ConversionService conversionService;
    private final ApplicationContext context;
    private final UserHolder userHolder;
    private final Generator generator;

    private final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    public ReportServiceImpl(ReportFileRepository reportFileRepository,
                             ReportRepository reportRepository,
                             ConversionService conversionService,
                             ApplicationContext context,
                             UserHolder userHolder,
                             Generator generator) {
        this.reportFileRepository = reportFileRepository;
        this.reportRepository = reportRepository;
        this.conversionService = conversionService;
        this.context = context;
        this.userHolder = userHolder;
        this.generator = generator;
    }

    @Transactional
    @Override
    public void execute(ReportType type, Params params) {
        final ReportExecutionService reportExecutionService;
        String login = userHolder.getLoginFromContext();
        String description;

        switch (type) {
            case BALANCE:
                reportExecutionService = context.getBean(ReportBalanceExecutionService.class);
                description = "Отчёт по балансам, запрос от ";
                break;
            case BY_DATE:
                reportExecutionService = context.getBean(ReportOperationSortByParamExecutionService.class);
                description = "Отчёт по операциям в разрезе дат, запрос от ";
                break;
            case BY_CATEGORY:
                reportExecutionService = context.getBean(ReportOperationSortByParamExecutionService.class);
                description = "Отчёт по операциям в разрезе категорий, запрос от ";
                break;
            default:
                throw new ServerException("Отсутствует реализация отчёта " + type);
        }

        params.setSort(type);

        UUID id = generator.generateUUID();
        LocalDateTime timeNow = generator.now();
        String date = timeNow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        Report report = Report.Builder.createBuilder()
                .setId(id)
                .setDtCreate(timeNow)
                .setDtUpdate(timeNow)
                .setStatus(Status.LOADED)
                .setType(type)
                .setDescription(description + date)
                .setParams(params)
                .setUser(login)
                .build();

        ByteArrayOutputStream data = null;

        try {
            data = reportExecutionService.execute(params);
            report.setStatus(Status.DONE);
        } catch (Exception e) {
            logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            report.setStatus(Status.ERROR);
        }

        if (data != null) {
            ReportFile reportFile = new ReportFile(id, data, login);
            ReportFileEntity reportFileEntity = conversionService.convert(reportFile, ReportFileEntity.class);
            reportFileRepository.save(reportFileEntity);
        }

        params.setSort(null);
        ReportEntity reportEntity = conversionService.convert(report, ReportEntity.class);
        reportRepository.save(reportEntity);
    }

    @Override
    public Page<Report> get(Pageable pageable) {
        String login = userHolder.getLoginFromContext();

        Page<ReportEntity> entities = reportRepository.findByUserOrderByDtCreateAsc(login, pageable);

        List<Report> reports = entities.stream()
                .map(entity -> conversionService.convert(entity, Report.class))
                .collect(Collectors.toList());

        return new PageImpl<>(reports, pageable, entities.getTotalElements());
    }

    @Override
    public ByteArrayOutputStream download(UUID id) {
        String login = this.userHolder.getLoginFromContext();

        ReportFileEntity fileEntity = reportFileRepository.findByUserAndId(login, id).orElse(null);

        if (fileEntity == null) {
            return null;
        }

        ReportFile reportFile = conversionService.convert(fileEntity, ReportFile.class);
        return reportFile.getData();
    }

    @Override
    public boolean isReportReady(UUID id) {
        String login = userHolder.getLoginFromContext();
        String status = Status.DONE.toString();
        return reportRepository.findByUserAndIdAndStatus(login, id, status).isPresent();
    }
}
