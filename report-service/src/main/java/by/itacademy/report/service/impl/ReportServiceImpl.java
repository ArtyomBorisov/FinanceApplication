package by.itacademy.report.service.impl;

import by.itacademy.report.dto.Params;
import by.itacademy.report.dto.Report;
import by.itacademy.report.constant.ReportType;
import by.itacademy.report.constant.Status;
import by.itacademy.report.dao.ReportRepository;
import by.itacademy.report.dao.entity.ReportEntity;
import by.itacademy.report.service.*;
import by.itacademy.report.utils.Generator;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final FileStorageService storageService;
    private final ReportExecutionService reportExecutionService;
    private final ReportRepository reportRepository;
    private final ConversionService conversionService;
    private final UserHolder userHolder;
    private final Generator generator;

    public ReportServiceImpl(FileStorageService storageService,
                             ReportExecutionService reportExecutionService,
                             ReportRepository reportRepository,
                             ConversionService conversionService,
                             UserHolder userHolder,
                             Generator generator) {
        this.storageService = storageService;
        this.reportExecutionService = reportExecutionService;
        this.reportRepository = reportRepository;
        this.conversionService = conversionService;
        this.userHolder = userHolder;
        this.generator = generator;
    }

    @Transactional
    @Override
    public void createReportExecutionTask(ReportType type, Params params) {
        Report report = createReportDto(type, params);
        ReportEntity reportEntity = conversionService.convert(report, ReportEntity.class);
        reportRepository.save(reportEntity);
        reportExecutionService.startExecution(type, params, report.getId());
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
    public byte[] download(UUID id) {
        return isReportReady(id) ?
                storageService.download(id.toString()).getData() :
                new byte[0];
    }

    @Override
    public boolean isReportReady(UUID id) {
        String login = userHolder.getLoginFromContext();
        String status = Status.DONE.toString();
        return reportRepository.findByUserAndIdAndStatus(login, id, status).isPresent();
    }

    private Report createReportDto(ReportType type, Params params) {
        String login = userHolder.getLoginFromContext();
        UUID id = generator.generateUUID();
        LocalDateTime timeNow = generator.now();

        return Report.Builder.createBuilder()
                .setId(id)
                .setDtCreate(timeNow)
                .setDtUpdate(timeNow)
                .setStatus(Status.LOADED)
                .setType(type)
                .setDescription(getDescription(type, timeNow))
                .setParams(params)
                .setUser(login)
                .build();
    }

    private String getDescription(ReportType reportType, LocalDateTime ldt) {
        String finalTime = ldt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        switch (reportType) {
            case BALANCE:
                return "Отчёт по балансам, запрос от " + finalTime;

            case BY_DATE:
                return "Отчёт по операциям в разрезе дат, запрос от " + finalTime;

            case BY_CATEGORY:
                return "Отчёт по операциям в разрезе категорий, запрос от " + finalTime;

            default:
                return "Отчёт";
        }
    }
}
