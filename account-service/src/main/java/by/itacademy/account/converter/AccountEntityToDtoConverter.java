package by.itacademy.account.converter;

import by.itacademy.account.constant.AccountType;
import by.itacademy.account.dto.Account;
import by.itacademy.account.dao.entity.AccountEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountEntityToDtoConverter implements Converter<AccountEntity, Account> {
    @Override
    public Account convert(AccountEntity entity) {
        return Account.Builder.createBuilder()
                .setId(entity.getId())
                .setDtCreate(entity.getDtCreate())
                .setDtUpdate(entity.getDtUpdate())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setBalance(entity.getBalance() == null ? null : entity.getBalance().getSum())
                .setType(AccountType.valueOf(entity.getType()))
                .setCurrency(entity.getCurrency())
                .setUser(entity.getUser())
                .build();
    }
}
