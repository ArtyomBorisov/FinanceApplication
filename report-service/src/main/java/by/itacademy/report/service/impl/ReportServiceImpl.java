package by.itacademy.report.service.impl;

import by.itacademy.report.dto.Params;
import by.itacademy.report.dto.Report;
import by.itacademy.report.dto.FileData;
import by.itacademy.report.constant.ReportType;
import by.itacademy.report.constant.Status;
import by.itacademy.report.dao.ReportRepository;
import by.itacademy.report.dao.entity.ReportEntity;
import by.itacademy.report.service.*;
import by.itacademy.report.utils.Generator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final FileStorageService storageService;
    private final ReportExecutionServiceFactory factory;
    private final ReportRepository reportRepository;
    private final ConversionService conversionService;
    private final UserHolder userHolder;
    private final Generator generator;

    private final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    public ReportServiceImpl(FileStorageService storageService,
                             ReportExecutionServiceFactory factory,
                             ReportRepository reportRepository,
                             ConversionService conversionService,
                             UserHolder userHolder,
                             Generator generator) {
        this.storageService = storageService;
        this.factory = factory;
        this.reportRepository = reportRepository;
        this.conversionService = conversionService;
        this.userHolder = userHolder;
        this.generator = generator;
    }

    @Transactional
    @Override
    public void execute(ReportType type, Params params) {
        String login = userHolder.getLoginFromContext();
        UUID id = generator.generateUUID();
        LocalDateTime timeNow = generator.now();

        final ReportExecutionService reportExecutionService = factory.getService(type);
        String description = factory.getDescription(type, timeNow);

        Report report = Report.Builder.createBuilder()
                .setId(id)
                .setDtCreate(timeNow)
                .setDtUpdate(timeNow)
                .setStatus(Status.LOADED)
                .setType(type)
                .setDescription(description)
                .setParams(params)
                .setUser(login)
                .build();

        params.setSort(type);
        try {
            byte[] bytes = reportExecutionService.execute(params);
            FileData fileData = new FileData(id.toString(), bytes);
            storageService.upload(fileData);
            report.setStatus(Status.DONE);
        } catch (Exception e) {
            logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            report.setStatus(Status.ERROR);
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
}
