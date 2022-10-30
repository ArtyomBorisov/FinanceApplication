package by.itacademy.report.utils.converter;

import by.itacademy.report.exception.MessageError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter
public class MapToStringConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper mapper;

    public MapToStringConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(MessageError.CONVERTER_ERROR);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        try {
            return mapper.readValue(str, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(MessageError.CONVERTER_ERROR);
        }
    }
}
