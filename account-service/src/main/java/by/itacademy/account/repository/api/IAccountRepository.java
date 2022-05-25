package by.itacademy.account.repository.api;

import by.itacademy.account.repository.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByTitle(String title);
    List<AccountEntity> findByOrderByDtCreateAsc();
    List<AccountEntity> findByOrderByTitleAsc();
    List<AccountEntity> findByIdInOrderByTitleAsc(Collection<UUID> uuids);
}
