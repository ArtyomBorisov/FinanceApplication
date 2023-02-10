package by.itacademy.report.converter;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.dto.Params;
import by.itacademy.report.exception.ServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ParamsToStringConverter implements AttributeConverter<Params, String> {

    private final ObjectMapper mapper;

    public ParamsToStringConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(Params params) {
        if (params == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new ServerException(MessageError.CONVERTER_ERROR);
        }
    }

    @Override
    public Params convertToEntityAttribute(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        try {
            return mapper.readValue(str, Params.class);
        } catch (JsonProcessingException e) {
            throw new ServerException(MessageError.CONVERTER_ERROR);
        }
    }
}
