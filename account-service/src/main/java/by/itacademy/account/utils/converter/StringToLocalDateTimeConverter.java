package by.itacademy.account.utils.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String str) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(str)),
                        TimeZone.getDefault().toZoneId());
    }
}
