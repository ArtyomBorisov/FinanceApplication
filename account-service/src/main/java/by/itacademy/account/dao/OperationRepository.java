package by.itacademy.account.dao;

import by.itacademy.account.dao.entity.OperationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, UUID>,
        JpaSpecificationExecutor<OperationEntity> {

    Page<OperationEntity> findByAccountEntity_IdOrderByDtCreateAsc(UUID idAccount, Pageable pageable);
    Optional<OperationEntity> findByIdAndAccountEntity_Id(UUID idOperation, UUID idAccount);

    static Specification<OperationEntity> accountsIdIn(Set<UUID> accounts) {
        return (entity, cq, cb) -> cb.in(entity.get("account.id")).value(accounts);
    }

    static Specification<OperationEntity> categoriesIdIn(Set<UUID> categories) {
        return (entity, cq, cb) -> cb.in(entity.get("category")).value(categories);
    }

    static Specification<OperationEntity> dateGreaterThan(LocalDateTime from) {
        return (entity, cq, cb) -> cb.greaterThan(entity.get("date"), from);
    }

    static Specification<OperationEntity> dateLessThan(LocalDateTime to) {
        return (entity, cq, cb) -> cb.lessThan(entity.get("date"), to);
    }
}
