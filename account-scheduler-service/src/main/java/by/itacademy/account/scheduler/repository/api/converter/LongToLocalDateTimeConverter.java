package by.itacademy.account.scheduler.repository.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
public class LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
    @Override
    public LocalDateTime convert(Long num) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(num),
                        TimeZone.getDefault().toZoneId());
    }
}
