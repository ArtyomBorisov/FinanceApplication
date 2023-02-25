package by.itacademy.mail.scheduler.service.impl;

import by.itacademy.mail.scheduler.dao.MonthlyReportRepository;
import by.itacademy.mail.scheduler.dao.entity.MonthlyReportEntity;
import by.itacademy.mail.scheduler.dto.MonthlyReport;
import by.itacademy.mail.scheduler.exception.MonthlyReportExistException;
import by.itacademy.mail.scheduler.service.MonthlyReportService;
import by.itacademy.mail.scheduler.service.UserHolder;
import by.itacademy.mail.scheduler.utils.Generator;
import org.springframework.stereotype.Service;

@Service
public class MonthlyReportServiceImpl implements MonthlyReportService {

    private final MonthlyReportRepository repository;
    private final UserHolder userHolder;
    private final Generator generator;

    public MonthlyReportServiceImpl(MonthlyReportRepository repository,
                                    UserHolder userHolder,
                                    Generator generator) {
        this.repository = repository;
        this.userHolder = userHolder;
        this.generator = generator;
    }

    @Override
    public void add(MonthlyReport monthlyReport) {
        String login = userHolder.getLoginFromContext();

        if (repository.existsById(login)) {
            throw new MonthlyReportExistException();
        }

        MonthlyReportEntity reportForSaving = createEntity(login, monthlyReport);
        repository.save(reportForSaving);
    }

    @Override
    public MonthlyReport get() {
        String login = userHolder.getLoginFromContext();

        MonthlyReportEntity foundEntity = repository.findById(login).orElse(null);

        return foundEntity != null ?
                new MonthlyReport(foundEntity.getEmail(), foundEntity.getReportType()) :
                null;
    }

    @Override
    public void delete() {
        String login = userHolder.getLoginFromContext();

        repository.findById(login).ifPresent(repository::delete);
    }

    private MonthlyReportEntity createEntity(String login, MonthlyReport report) {
        return MonthlyReportEntity.Builder.createBuilder()
                .setLogin(login)
                .setDtCreate(generator.dateTimeNow())
                .setEmail(report.getEmail())
                .setReportType(report.getReportType())
                .build();
    }
}
