package by.itacademy.report.service.impl.execution;

import by.itacademy.report.constant.ReportType;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.service.ReportExecutionService;
import by.itacademy.report.service.ReportExecutionServiceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReportExecutionServiceFactoryImpl implements ReportExecutionServiceFactory {

    private final ApplicationContext context;

    public ReportExecutionServiceFactoryImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public ReportExecutionService getService(ReportType reportType) {
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

    @Override
    public String getDescription(ReportType reportType, LocalDateTime ldt) {
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
