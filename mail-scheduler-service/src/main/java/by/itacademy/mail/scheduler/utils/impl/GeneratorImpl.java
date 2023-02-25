package by.itacademy.mail.scheduler.utils.impl;

import by.itacademy.mail.scheduler.utils.Generator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class GeneratorImpl implements Generator {

    @Override
    public LocalDateTime dateTimeNow() {
        return LocalDateTime.now();
    }

    @Override
    public LocalDate dateNow() {
        return LocalDate.now();
    }
}
