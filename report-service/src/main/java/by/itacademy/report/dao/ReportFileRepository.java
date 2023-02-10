package by.itacademy.report.dao;

import by.itacademy.report.dao.entity.ReportFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// временное решение, в будущем перейду на minio
@Repository
public interface ReportFileRepository extends JpaRepository<ReportFileEntity, UUID> {
    Optional<ReportFileEntity> findByUserAndId(String login, UUID id);
}
