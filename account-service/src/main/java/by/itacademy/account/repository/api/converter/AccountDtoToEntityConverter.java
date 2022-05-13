package by.itacademy.account.repository.api.converter;

import by.itacademy.account.model.Account;
import by.itacademy.account.repository.api.IBalanceRepository;
import by.itacademy.account.repository.entity.AccountEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoToEntityConverter implements Converter<Account, AccountEntity> {

    private final IBalanceRepository balanceRepository;

    public AccountDtoToEntityConverter(IBalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    public AccountEntity convert(Account dto) {
        return AccountEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setDtCreate(dto.getDtCreate())
                .setDtUpdate(dto.getDtUpdate())
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setBalance(this.balanceRepository.findById(dto.getId()).orElse(null))
                .setType(dto.getType() == null ? null : dto.getType().toString())
                .setCurrency(dto.getCurrency())
                .build();
    }
}
