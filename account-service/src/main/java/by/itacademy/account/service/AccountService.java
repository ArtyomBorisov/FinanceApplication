package by.itacademy.account.service;

import by.itacademy.account.model.Account;
import by.itacademy.account.repository.api.IAccountRepository;
import by.itacademy.account.repository.api.IBalanceRepository;
import by.itacademy.account.repository.entity.AccountEntity;
import by.itacademy.account.repository.entity.BalanceEntity;
import by.itacademy.account.service.api.IAccountService;
import by.itacademy.account.service.api.ValidationError;
import by.itacademy.account.service.api.ValidationException;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AccountService implements IAccountService {

    private final IAccountRepository accountRepository;
    private final IBalanceRepository balanceRepository;
    private final ConversionService conversionService;
    private final RestTemplate restTemplate;

    public AccountService(IAccountRepository accountRepository,
                          IBalanceRepository balanceRepository,
                          ConversionService conversionService) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.conversionService = conversionService;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public Account add(Account account) {
        List<ValidationError> errors = new ArrayList<>();

        this.checkAccount(account, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        UUID uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        account.setId(uuid);
        account.setDtCreate(now);
        account.setDtUpdate(now);

        try {
            this.balanceRepository.save(BalanceEntity.Builder
                    .createBuilder()
                    .setId(uuid)
                    .setDtUpdate(now)
                    .setSum(0)
                    .build());
            this.accountRepository.save(
                    this.conversionService.convert(account, AccountEntity.class));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.get(uuid);
    }

    @Override
    public Page<Account> get(Pageable pageable) {
        List<AccountEntity> entities;

        try {
            entities = this.accountRepository.findByOrderByDtCreateAsc();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        List<Account> result = entities.stream()
                .map(entity -> this.conversionService.convert(entity, Account.class))
                .collect(Collectors.toList());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        return new PageImpl<>(result.subList(start, end), pageable, result.size());
    }

    @Override
    public Account get(UUID id) {
        List<ValidationError> errors = new ArrayList<>();
        AccountEntity entity;

        this.checkIdAccount(id, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        try {
            entity = this.accountRepository.findById(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.conversionService.convert(entity, Account.class);
    }

    @Transactional
    @Override
    public Account update(Account account, UUID id, LocalDateTime dtUpdate) {
        List<ValidationError> errors = new ArrayList<>();
        AccountEntity entity = null;

        this.checkAccount(account, errors);
        this.checkIdAccount(id, errors);

        if (id != null) {
            try {
                entity = this.accountRepository.findById(id).orElse(null);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка выполнения SQL", e);
            }
        }

        if (dtUpdate == null) {
            errors.add(new ValidationError("dtUpdate",
                    "Не передан параметр параметр последнего обновления"));
        } else if (entity != null && dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            errors.add(new ValidationError("dtUpdate",
                    "Передан неверный параметр параметр последнего обновления"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        entity.setCurrency(account.getCurrency());
        entity.setDescription(account.getDescription());
        entity.setTitle(account.getTitle());
        entity.setType(account.getType().toString());

        try {
            this.accountRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.get(id);
    }

    @Override
    public boolean isAccountExist(UUID id) {
        if (id == null) {
            throw new ValidationException("Не передан id аккаунта");
        }

        try {
            return this.accountRepository.findById(id).isPresent();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }
    }

    private void checkIdAccount(UUID idAccount, List<ValidationError> errors) {
        if (idAccount == null) {
            errors.add(new ValidationError("idAccount", "Не передан id счёта"));
            return;
        }

        try {
            if (this.accountRepository.findById(idAccount).isEmpty()) {
                errors.add(new ValidationError("id", "Передан id несуществующего счёта"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }
    }

    private void checkAccount(Account account, List<ValidationError> errors) {
        String currencyClassifierUrl = "http://localhost:8081/classifier/currency/" + account.getCurrency() + "/";

        if (account == null) {
            errors.add(new ValidationError("account", "Не передан объект account"));
            return;
        }

        if (account.getType() == null) {
            errors.add(new ValidationError("type", "Не передан тип счёта"));
        }

        if (account.getCurrency() == null) {
            errors.add(new ValidationError("currency", "Не передана валюта счёта"));
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(headers);
            try {
                this.restTemplate.exchange(currencyClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError("currency", "Передан id валюты, которой нет в справочнике"));
            }
        }

        if (account.getTitle() == null || account.getTitle().isEmpty()) {
            errors.add(new ValidationError("title", "Не передано название счёта (или передано пустое)"));
        } else {
            try {
                if (this.accountRepository.findByTitle(account.getTitle()).isPresent()) {
                    errors.add(new ValidationError("title", "Передано не уникальное название счёта"));
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка выполнения SQL", e);
            }
        }
    }
}
