package by.itacademy.account.converter;

import by.itacademy.account.dto.Operation;
import by.itacademy.account.dao.entity.OperationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationDtoToEntityConverter implements Converter<Operation, OperationEntity> {
    @Override
    public OperationEntity convert(Operation dto) {
        return OperationEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setDtCreate(dto.getDtCreate())
                .setDtUpdate(dto.getDtUpdate())
                .setDate(dto.getDate())
                .setDescription(dto.getDescription())
                .setCategory(dto.getCategory())
                .setValue(dto.getValue())
                .setCurrency(dto.getCurrency())
                .setAccountEntity(null)
                .build();
    }
}
