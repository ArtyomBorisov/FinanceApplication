package by.itacademy.account.repository.api.converter;

import by.itacademy.account.model.Operation;
import by.itacademy.account.repository.entity.OperationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationDtoToEntityConverter implements Converter<Operation, OperationEntity> {

    private final AccountDtoToEntityConverter accountDtoToEntityConverter;

    public OperationDtoToEntityConverter(AccountDtoToEntityConverter accountDtoToEntityConverter) {
        this.accountDtoToEntityConverter = accountDtoToEntityConverter;
    }

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
                .setAccountEntity(dto.getAccount() == null ?
                        null : this.accountDtoToEntityConverter.convert(dto.getAccount()))
                .build();
    }
}
