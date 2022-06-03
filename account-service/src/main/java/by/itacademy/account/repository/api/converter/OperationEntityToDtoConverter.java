package by.itacademy.account.repository.api.converter;

import by.itacademy.account.model.Operation;
import by.itacademy.account.repository.entity.OperationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationEntityToDtoConverter implements Converter<OperationEntity, Operation> {
    @Override
    public Operation convert(OperationEntity entity) {
        return Operation.Builder.createBuilder()
                .setId(entity.getId())
                .setDtCreate(entity.getDtCreate())
                .setDtUpdate(entity.getDtUpdate())
                .setDate(entity.getDate())
                .setDescription(entity.getDescription())
                .setCategory(entity.getCategory())
                .setValue(entity.getValue())
                .setCurrency(entity.getCurrency())
                .setAccount(null)
                .build();
    }
}
