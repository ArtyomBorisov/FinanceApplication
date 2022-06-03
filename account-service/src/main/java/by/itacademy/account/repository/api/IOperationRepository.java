package by.itacademy.account.repository.api;

import by.itacademy.account.repository.entity.OperationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOperationRepository extends JpaRepository<OperationEntity, UUID> {
    Page<OperationEntity> findByAccountEntity_IdOrderByDtCreateAsc(UUID idAccount, Pageable pageable);
    Optional<OperationEntity> findByIdAndAccountEntity_Id(UUID idOperation, UUID idAccount);

    Page<OperationEntity> findByAccountEntity_IdInAndCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(
            List<UUID> accounts,
            List<UUID> categories,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);

    Page<OperationEntity> findByAccountEntity_UserAndCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(
            String login,
            List<UUID> categories,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);

    Page<OperationEntity> findByAccountEntity_IdInAndDateGreaterThanEqualAndDateLessThanEqual(List<UUID> accounts,
                                                                                              LocalDateTime from,
                                                                                              LocalDateTime to,
                                                                                              Pageable pageable);

    Page<OperationEntity> findByAccountEntity_UserAndDateGreaterThanEqualAndDateLessThanEqual(String login,
                                                                                              LocalDateTime from,
                                                                                              LocalDateTime to,
                                                                                              Pageable pageable);
}
