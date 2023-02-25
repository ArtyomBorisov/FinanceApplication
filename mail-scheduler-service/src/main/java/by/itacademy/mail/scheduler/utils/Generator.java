package by.itacademy.mail.scheduler.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Generator {
    LocalDateTime dateTimeNow();
    LocalDate dateNow();
}
