package by.itacademy.account.repository.api;

import by.itacademy.account.repository.entity.OperationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOperationRepository extends JpaRepository<OperationEntity, UUID> {
    List<OperationEntity> findByAccountEntity_IdOrderByDtCreateAsc(UUID idAccount);
    Optional<OperationEntity> findByIdAndAccountEntity_Id(UUID idOperation, UUID idAccount);


    List<OperationEntity> findByAccountEntity_IdInAndCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(
            List<UUID> accounts,
            List<UUID> categories,
            LocalDateTime from,
            LocalDateTime to,
            Sort sort);

    List<OperationEntity> findByCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(List<UUID> categories,
                                                                                      LocalDateTime from,
                                                                                      LocalDateTime to,
                                                                                      Sort sort);

    List<OperationEntity> findByAccountEntity_IdInAndDateGreaterThanEqualAndDateLessThanEqual(List<UUID> accounts,
                                                                                              LocalDateTime from,
                                                                                              LocalDateTime to,
                                                                                              Sort sort);

    List<OperationEntity> findByDateGreaterThanEqualAndDateLessThanEqual(LocalDateTime from,
                                                                         LocalDateTime to,
                                                                         Sort sort);
}
