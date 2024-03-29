package by.itacademy.account.converter;

import by.itacademy.account.dto.Account;
import by.itacademy.account.dao.entity.AccountEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoToEntityConverter implements Converter<Account, AccountEntity> {
    @Override
    public AccountEntity convert(Account dto) {
        return AccountEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setDtCreate(dto.getDtCreate())
                .setDtUpdate(dto.getDtUpdate())
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setBalance(null)
                .setType(dto.getType() == null ? null : dto.getType().toString())
                .setCurrency(dto.getCurrency())
                .setUser(dto.getUser())
                .build();
    }
}
