package by.itacademy.classifier.repository;

import by.itacademy.classifier.repository.entity.CurrencyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {
    Optional<CurrencyEntity> findById(UUID id);
    Optional<CurrencyEntity> findByTitle(String title);
    Page<CurrencyEntity> findByIdInOrderByTitle(Collection<UUID> collectionId, Pageable pageable);
    Page<CurrencyEntity> findByOrderByTitle(Pageable pageable);
    boolean existsCategoryEntityById(UUID id);
}
