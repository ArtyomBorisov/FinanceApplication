package by.itacademy.classifier.repository.api;

import by.itacademy.classifier.repository.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    Optional<CategoryEntity> findByTitle(String title);
    Collection<CategoryEntity> findByIdInOrderByTitle(Collection<UUID> collectionId);
    Collection<CategoryEntity> findByOrderByTitle();
}
