package by.itacademy.account.repository.api.converter;

import by.itacademy.account.model.Account;
import by.itacademy.account.model.api.Type;
import by.itacademy.account.repository.entity.AccountEntity;
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
                .setType(Type.valueOf(entity.getType()))
                .setCurrency(entity.getCurrency())
                .setUser(entity.getUser())
                .build();
    }
}
