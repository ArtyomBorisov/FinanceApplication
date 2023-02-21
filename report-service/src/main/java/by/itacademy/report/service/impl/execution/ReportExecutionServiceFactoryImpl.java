package by.itacademy.report.service.impl.execution;

import by.itacademy.report.constant.ReportType;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.service.ReportDataExecutionService;
import by.itacademy.report.service.ReportExecutionServiceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ReportExecutionServiceFactoryImpl implements ReportExecutionServiceFactory {

    private final ApplicationContext context;

    public ReportExecutionServiceFactoryImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public ReportDataExecutionService getService(ReportType reportType) {
        switch (reportType) {
            case BALANCE:
                return context.getBean(ReportBalanceExecutionService.class);

            case BY_CATEGORY:
            case BY_DATE:
                return context.getBean(ReportOperationSortByParamExecutionService.class);

            default:
                throw new ServerException("Отсутствует реализация отчёта " + reportType);
        }
    }
}
