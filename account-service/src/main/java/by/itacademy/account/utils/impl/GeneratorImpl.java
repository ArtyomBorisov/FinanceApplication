package by.itacademy.account.utils.impl;

import by.itacademy.account.utils.Generator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class GeneratorImpl implements Generator {
    @Override
    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
