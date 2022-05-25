package by.itacademy.account.service.api;

import by.itacademy.account.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public interface IAccountService {
    Account add(Account account);
    Page<Account> get(Pageable pageable);
    Page<Account> getInOrderByTitle(Collection<UUID> uuids, Pageable pageable);
    Account get(UUID id);
    Account update(Account account, UUID id, LocalDateTime dtUpdate);
    boolean isAccountExist(UUID id);
}
