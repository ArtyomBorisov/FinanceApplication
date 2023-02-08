package by.itacademy.account.dao;

import by.itacademy.account.dao.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByUserAndId(String login, UUID id);
    Optional<AccountEntity> findByUserAndTitle(String login, String title);
    Page<AccountEntity> findByUserOrderByBalance_SumDesc(String login, Pageable pageable);
    Page<AccountEntity> findByUserAndIdInOrderByBalance_SumDesc(String login, Collection<UUID> uuids, Pageable pageable);
    boolean existsAccountEntityByUserAndId(String login, UUID id);
}
