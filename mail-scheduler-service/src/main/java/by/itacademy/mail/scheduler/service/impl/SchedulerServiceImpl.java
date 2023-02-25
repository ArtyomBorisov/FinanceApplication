package by.itacademy.mail.scheduler.service.impl;

import by.itacademy.mail.scheduler.dao.MonthlyReportRepository;
import by.itacademy.mail.scheduler.dao.entity.MonthlyReportEntity;
import by.itacademy.mail.scheduler.dto.LoginReportNameEmailDto;
import by.itacademy.mail.scheduler.service.SchedulerService;
import by.itacademy.mail.scheduler.service.SenderService;
import by.itacademy.mail.scheduler.utils.Generator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final MonthlyReportRepository repository;
    private final SenderService senderService;
    private final Generator generator;

    public SchedulerServiceImpl(MonthlyReportRepository repository,
                                SenderService senderService,
                                Generator generator) {
        this.repository = repository;
        this.senderService = senderService;
        this.generator = generator;
    }

    @Scheduled(cron = "0 0 2 2 * ?")
    @Override
    public void prepareReports() {
        getAll().forEach(entity -> {
            String reportName = senderService.prepareReport(entity.getLogin(), entity.getReportType());
            entity.setLastReport(reportName);
            repository.save(entity);
        });
    }

    @Scheduled(cron = "0 0 2 3 * ?")
    @Override
    public void sendEmails() {
        getAll().forEach(entity -> senderService.send(convertToDto(entity)));
    }

    private Collection<MonthlyReportEntity> getAll() {
        return repository.findByDtCreateLessThan(getLastDateTimeOfPreviousMonth());
    }

    private LoginReportNameEmailDto convertToDto(MonthlyReportEntity entity) {
        return LoginReportNameEmailDto.Builder.createBuilder()
                .setLogin(entity.getLogin())
                .setEmail(entity.getEmail())
                .setReportName(entity.getLastReport())
                .build();
    }

    private LocalDateTime getLastDateTimeOfPreviousMonth() {
        LocalDate dateWithRequiredMonth = generator.dateNow().minusMonths(1);
        int lastDay = dateWithRequiredMonth.getMonth().length(dateWithRequiredMonth.isLeapYear());
        LocalDate lastDayOfRequiredMonth = dateWithRequiredMonth.withDayOfMonth(lastDay);
        return LocalDateTime.of(lastDayOfRequiredMonth, LocalTime.MAX);
    }
}
