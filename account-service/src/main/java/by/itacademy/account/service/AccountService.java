package by.itacademy.account.service;

import by.itacademy.account.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.account.model.Account;
import by.itacademy.account.repository.api.IAccountRepository;
import by.itacademy.account.repository.api.IBalanceRepository;
import by.itacademy.account.repository.entity.AccountEntity;
import by.itacademy.account.repository.entity.BalanceEntity;
import by.itacademy.account.service.api.IAccountService;
import by.itacademy.account.service.api.MessageError;
import by.itacademy.account.service.api.ValidationError;
import by.itacademy.account.service.api.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AccountService implements IAccountService {

    @Value("${classifier_currency_url}")
    private String currencyUrl;

    private final IAccountRepository accountRepository;
    private final IBalanceRepository balanceRepository;
    private final ConversionService conversionService;
    private final RestTemplate restTemplate;
    private final UserHolder userHolder;

    public AccountService(IAccountRepository accountRepository,
                          IBalanceRepository balanceRepository,
                          ConversionService conversionService,
                          UserHolder userHolder) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.conversionService = conversionService;
        this.userHolder = userHolder;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public Account add(Account account) {
        String login = this.userHolder.getLoginFromContext();

        this.checkAccountWithTitleUnique(account);

        UUID uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        account.setId(uuid);
        account.setDtCreate(now);
        account.setDtUpdate(now);
        account.setUser(login);

        AccountEntity saveEntity;

        try {
            BalanceEntity balanceEntity = this.balanceRepository.save(BalanceEntity.Builder
                    .createBuilder()
                    .setId(uuid)
                    .setDtUpdate(now)
                    .setSum(0)
                    .build());
            saveEntity = this.accountRepository.save(
                    this.conversionService.convert(account, AccountEntity.class).setBalance(balanceEntity));
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return this.conversionService.convert(saveEntity, Account.class);
    }

    @Override
    public Page<Account> get(Pageable pageable) {
        String login = this.userHolder.getLoginFromContext();

        Page<AccountEntity> entities;

        try {
            entities = this.accountRepository.findByUserOrderByBalance_SumDesc(login, pageable);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Account.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Page<Account> get(Collection<UUID> uuids, Pageable pageable) {
        String login = this.userHolder.getLoginFromContext();

        Page<AccountEntity> entities;

        if (uuids == null || uuids.isEmpty()) {
            entities = this.accountRepository.findByUserOrderByBalance_SumDesc(login, pageable);
        } else {
            this.checkCollectionIdAccount(uuids);

            entities = this.accountRepository.findByUserAndIdInOrderByBalance_SumDesc(login, uuids, pageable);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Account.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Account get(UUID id) {
        String login = this.userHolder.getLoginFromContext();

        this.checkIdAccount(id);

        AccountEntity entity;

        try {
            entity = this.accountRepository.findByUserAndId(login, id).get();
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return this.conversionService.convert(entity, Account.class);
    }

    @Transactional
    @Override
    public Account update(Account account, UUID id, LocalDateTime dtUpdate) {
        AccountEntity entity = this.get(account, id, dtUpdate);

        entity.setCurrency(account.getCurrency());
        entity.setDescription(account.getDescription());
        entity.setTitle(account.getTitle());
        entity.setType(account.getType().toString());

        AccountEntity saveEntity;

        try {
            saveEntity = this.accountRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return this.conversionService.convert(saveEntity, Account.class);
    }

    @Override
    public boolean isAccountExist(UUID id) {
        String login = this.userHolder.getLoginFromContext();

        if (id == null) {
            throw new ValidationException(new ValidationError("id счёта", MessageError.MISSING_FIELD));
        }

        try {
            return this.accountRepository.existsAccountEntityByUserAndId(login, id);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }

    private void checkAccountWithTitleUnique(Account account) {
        String login = this.userHolder.getLoginFromContext();

        List<ValidationError> errors = new ArrayList<>();

        this.checkAccount(account, errors);

        try {
            if (this.accountRepository.findByUserAndTitle(login, account.getTitle()).isPresent()) {
                errors.add(new ValidationError("title (название счёта)", MessageError.NO_UNIQUE_FIELD));
            }
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void checkIdAccount(UUID idAccount, List<ValidationError> errors) {
        String login = this.userHolder.getLoginFromContext();

        if (idAccount == null) {
            errors.add(new ValidationError("id счёта", MessageError.MISSING_FIELD));
            return;
        }

        try {
        if (!this.accountRepository.existsAccountEntityByUserAndId(login, idAccount)) {
                errors.add(new ValidationError("id счёта", MessageError.ID_NOT_EXIST));
            }
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }

    private void checkIdAccount(UUID idAccount) {
        List<ValidationError> errors = new ArrayList<>();

        this.checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void checkAccount(Account account, List<ValidationError> errors) {
        if (account == null) {
            errors.add(new ValidationError("account (счёт)", MessageError.MISSING_OBJECT));
            return;
        }

        if (account.getType() == null) {
            errors.add(new ValidationError("type (тип счёта)", MessageError.MISSING_FIELD));
        }

        if (account.getCurrency() == null) {
            errors.add(new ValidationError("id currency (валюта счёта)", MessageError.MISSING_FIELD));
        } else {
            String currencyClassifierUrl = this.currencyUrl + "/" + account.getCurrency();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String token = JwtTokenUtil.generateAccessToken(this.userHolder.getUser());
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(headers);

            try {
                this.restTemplate.exchange(currencyClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError("id currency (валюта счёта)", MessageError.ID_NOT_EXIST));
            }
        }

        if (account.getTitle() == null || account.getTitle().isEmpty()) {
            errors.add(new ValidationError("title (название счёта)", MessageError.MISSING_FIELD));
        }
    }

    private void checkCollectionIdAccount(Collection<UUID> collection) {
        String login = this.userHolder.getLoginFromContext();

        List<ValidationError> errors = new ArrayList<>();

        for (UUID id : collection) {
            if (!this.accountRepository.existsAccountEntityByUserAndId(login, id)) {
                errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private AccountEntity get(Account account, UUID id, LocalDateTime dtUpdate) {
        String login = this.userHolder.getLoginFromContext();

        List<ValidationError> errors = new ArrayList<>();
        AccountEntity entity = null;

        this.checkAccount(account, errors);
        this.checkIdAccount(id, errors);

        if (id != null) {
            try {
                entity = this.accountRepository.findByUserAndId(login, id).orElse(null);
            } catch (Exception e) {
                throw new RuntimeException(MessageError.SQL_ERROR, e);
            }
        }

        if (dtUpdate == null) {
            errors.add(new ValidationError("dtUpdate (параметр последнего обновления)", MessageError.MISSING_FIELD));
        } else if (entity != null && dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            errors.add(new ValidationError("dtUpdate", MessageError.INVALID_DT_UPDATE));
        }

        if (entity != null && account.getTitle() != null && !account.getTitle().isEmpty()
                && entity.getTitle().compareTo(account.getTitle()) != 0
                && this.accountRepository.findByUserAndTitle(login, account.getTitle()).isPresent()) {
            errors.add(new ValidationError("title (название счёта)", MessageError.NO_UNIQUE_FIELD));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return entity;
    }
}
