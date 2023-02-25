package by.itacademy.mail.scheduler.service;

import by.itacademy.mail.scheduler.dto.MonthlyReport;

public interface MonthlyReportService {
    void add(MonthlyReport monthlyReport);
    MonthlyReport get();
    void delete();
}
