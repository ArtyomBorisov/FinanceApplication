package by.itacademy.classifier.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

public class StringToUuidConverter implements Converter<String, UUID> {
    @Override
    public UUID convert(String str) {
        return UUID.fromString(str);
    }
}
