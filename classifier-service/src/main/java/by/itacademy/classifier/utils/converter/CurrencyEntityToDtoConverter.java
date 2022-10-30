package by.itacademy.classifier.utils.converter;

import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.repository.entity.CurrencyEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CurrencyEntityToDtoConverter implements Converter<CurrencyEntity, Currency> {
    @Override
    public Currency convert(CurrencyEntity entity) {
        return Currency.Builder.createBuilder()
                .setId(entity.getId())
                .setDtCreate(entity.getDtCreate())
                .setDtUpdate(entity.getDtUpdate())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .build();
    }
}
