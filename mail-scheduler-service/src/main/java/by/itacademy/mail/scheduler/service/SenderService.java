package by.itacademy.mail.scheduler.service;

import by.itacademy.mail.scheduler.constant.ReportType;
import by.itacademy.mail.scheduler.dto.LoginReportNameEmailDto;

public interface SenderService {
    String prepareReport(String login, ReportType reportType);
    void send(LoginReportNameEmailDto dto);
}
