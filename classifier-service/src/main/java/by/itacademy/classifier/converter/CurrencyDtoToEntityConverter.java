package by.itacademy.classifier.converter;

import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.dao.entity.CurrencyEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CurrencyDtoToEntityConverter implements Converter<Currency, CurrencyEntity> {
    @Override
    public CurrencyEntity convert(Currency dto) {
        return CurrencyEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setDtCreate(dto.getDtCreate())
                .setDtUpdate(dto.getDtUpdate())
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .build();
    }
}
