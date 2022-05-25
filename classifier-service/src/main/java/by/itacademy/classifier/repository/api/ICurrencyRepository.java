package by.itacademy.classifier.repository.api;

import by.itacademy.classifier.repository.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {
    Optional<CurrencyEntity> findByTitle(String title);
    Collection<CurrencyEntity> findByIdInOrderByTitle(Collection<UUID> collectionId);
    Collection<CurrencyEntity> findByOrderByTitle();
}
