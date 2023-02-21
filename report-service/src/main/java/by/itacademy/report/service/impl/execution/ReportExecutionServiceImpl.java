package by.itacademy.report.service.impl.execution;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.constant.ReportType;
import by.itacademy.report.constant.Status;
import by.itacademy.report.dao.ReportRepository;
import by.itacademy.report.dto.FileData;
import by.itacademy.report.dto.Params;
import by.itacademy.report.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class ReportExecutionServiceImpl implements ReportExecutionService {

    private final ReportExecutionServiceFactory factory;
    private final FileStorageService storageService;
    private final ReportRepository reportRepository;

    private final Logger logger = LogManager.getLogger(ReportExecutionServiceImpl.class);

    public ReportExecutionServiceImpl(ReportExecutionServiceFactory factory,
                                      FileStorageService storageService,
                                      ReportRepository reportRepository) {
        this.factory = factory;
        this.storageService = storageService;
        this.reportRepository = reportRepository;
    }

    @Async("SecurityTaskExecutor")
    @Override
    @Transactional
    public void startExecution(ReportType type, Params params, UUID idReport) {
        ReportDataExecutionService reportDataExecutionService = factory.getService(type);
        try {
            params.setSort(type);
            byte[] bytes = reportDataExecutionService.execute(params);
            FileData fileData = new FileData(idReport.toString(), bytes);
            storageService.upload(fileData);
            reportRepository.setStatusForReportEntity(idReport, Status.DONE.toString());
        } catch (Exception e) {
            logger.error("{} {}: {}",
                    MessageError.REPORT_MAKING_EXCEPTION, e.getClass().getSimpleName(), e.getMessage());
            reportRepository.setStatusForReportEntity(idReport, Status.ERROR.toString());
        }
    }
}
