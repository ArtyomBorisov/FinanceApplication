package by.itacademy.account.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import java.io.IOException;

@JsonComponent
public class CustomPageSerializer extends JsonSerializer<Page<?>> {
    @Override
    public void serialize(Page<?> page,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("number", page.getNumber());
        jsonGenerator.writeNumberField("size", page.getSize());
        jsonGenerator.writeNumberField("total_pages", page.getTotalPages());
        jsonGenerator.writeNumberField("total_elements", page.getTotalElements());
        jsonGenerator.writeBooleanField("first", page.isFirst());
        jsonGenerator.writeNumberField("number_of_elements", page.getNumberOfElements());
        jsonGenerator.writeBooleanField("last", page.isLast());
        jsonGenerator.writeObjectField("content", page.getContent());
        jsonGenerator.writeEndObject();
    }
}
