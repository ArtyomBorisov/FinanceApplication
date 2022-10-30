package by.itacademy.report.repository.api;

import by.itacademy.report.repository.entity.ReportFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IReportFileRepository extends JpaRepository<ReportFileEntity, UUID> {
    Optional<ReportFileEntity> findByUserAndId(String login, UUID id);
}
