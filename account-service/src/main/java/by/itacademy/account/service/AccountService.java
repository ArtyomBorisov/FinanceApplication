package by.itacademy.account.service;

import by.itacademy.account.dto.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public interface AccountService {
    Account add(Account account);
    Page<Account> get(Pageable pageable);
    Page<Account> get(Collection<UUID> uuids, Pageable pageable);
    Account get(UUID id);
    Account update(Account account, UUID id, LocalDateTime dtUpdate);
    boolean isAccountExist(UUID id);
}
