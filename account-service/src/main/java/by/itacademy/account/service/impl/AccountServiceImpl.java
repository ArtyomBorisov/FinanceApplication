package by.itacademy.account.service.impl;

import by.itacademy.account.dto.Account;
import by.itacademy.account.dao.AccountRepository;
import by.itacademy.account.dao.BalanceRepository;
import by.itacademy.account.dao.entity.AccountEntity;
import by.itacademy.account.dao.entity.BalanceEntity;
import by.itacademy.account.service.AccountService;
import by.itacademy.account.service.UserHolder;
import by.itacademy.account.utils.Generator;
import by.itacademy.account.exception.OptimisticLockException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final ConversionService conversionService;
    private final UserHolder userHolder;
    private final Generator generator;

    public AccountServiceImpl(AccountRepository accountRepository,
                              BalanceRepository balanceRepository,
                              ConversionService conversionService,
                              UserHolder userHolder,
                              Generator generator) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.conversionService = conversionService;
        this.userHolder = userHolder;
        this.generator = generator;
    }

    @Transactional
    @Override
    public Account add(Account account) {
        String login = userHolder.getLoginFromContext();

        UUID uuid = generator.generateUUID();
        LocalDateTime now = generator.now();

        account.setId(uuid);
        account.setDtCreate(now);
        account.setDtUpdate(now);
        account.setUser(login);

        BalanceEntity balanceEntity = BalanceEntity.Builder.createBuilder()
                .setId(uuid)
                .setDtUpdate(now)
                .setSum(0)
                .build();

        BalanceEntity savedBalance = balanceRepository.save(balanceEntity);
        AccountEntity accountEntity = conversionService.convert(account, AccountEntity.class);
        accountEntity.setBalance(savedBalance);
        AccountEntity savedAccount = accountRepository.save(accountEntity);
        return conversionService.convert(savedAccount, Account.class);
    }

    @Override
    public Page<Account> get(Pageable pageable) {
        String login = userHolder.getLoginFromContext();

        Page<AccountEntity> entities = accountRepository.findByUserOrderByBalance_SumDesc(login, pageable);

        List<Account> accounts = entities.stream()
                .map(entity -> conversionService.convert(entity, Account.class))
                .collect(Collectors.toList());

        return new PageImpl<>(accounts, pageable, entities.getTotalElements());
    }

    @Override
    public Page<Account> get(Collection<UUID> uuids, Pageable pageable) {
        String login = userHolder.getLoginFromContext();

        if (uuids == null || uuids.isEmpty()) {
            return get(pageable);
        }

        Page<AccountEntity> entities = accountRepository
                .findByUserAndIdInOrderByBalance_SumDesc(login, uuids, pageable);

        List<Account> accounts = entities.stream()
                .map(entity -> conversionService.convert(entity, Account.class))
                .collect(Collectors.toList());

        return new PageImpl<>(accounts, pageable, entities.getTotalElements());
    }

    @Override
    public Account get(UUID id) {
        String login = userHolder.getLoginFromContext();

        AccountEntity entity = accountRepository.findByUserAndId(login, id).orElse(null);

        return entity != null ? conversionService.convert(entity, Account.class) : null;
    }

    @Transactional
    @Override
    public Account update(Account account, UUID id, LocalDateTime dtUpdate) {
        String login = userHolder.getLoginFromContext();

        AccountEntity entity = accountRepository.findByUserAndId(login, id).orElse(null);

        if (entity == null) {
            return null;
        }

        if (entity.getDtUpdate().compareTo(dtUpdate) != 0) {
            throw new OptimisticLockException();
        }

        entity.setCurrency(account.getCurrency());
        entity.setDescription(account.getDescription());
        entity.setTitle(account.getTitle());
        entity.setType(account.getType().toString());

        AccountEntity updatedEntity = accountRepository.save(entity);

        return this.conversionService.convert(updatedEntity, Account.class);
    }

    @Override
    public boolean isAccountExist(UUID id) {
        String login = userHolder.getLoginFromContext();

        return accountRepository.existsAccountEntityByUserAndId(login, id);
    }

    private void checkDtUpdate(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1.compareTo(dateTime2) != 0) {
            throw new OptimisticLockException();
        }
    }
}
