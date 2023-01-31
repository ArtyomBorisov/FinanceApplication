package by.itacademy.classifier.repository;

import by.itacademy.classifier.repository.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    Optional<CategoryEntity> findById(UUID id);
    Optional<CategoryEntity> findByTitle(String title);
    Page<CategoryEntity> findByIdInOrderByTitle(Collection<UUID> collectionId, Pageable pageable);
    Page<CategoryEntity> findByOrderByTitle(Pageable pageable);
    boolean existsCategoryEntityById(UUID id);
}
