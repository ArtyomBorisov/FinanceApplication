package by.itacademy.account.repository.api;

import by.itacademy.account.repository.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByTitle(String title);
    Page<AccountEntity> findByOrderByDtCreateAsc(Pageable pageable);
    Page<AccountEntity> findByOrderByTitleAsc(Pageable pageable);
    Page<AccountEntity> findByIdInOrderByTitleAsc(Collection<UUID> uuids, Pageable pageable);
}
