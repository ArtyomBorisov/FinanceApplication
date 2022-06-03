package by.itacademy.report.repository.api;

import by.itacademy.report.repository.entity.ReportFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IReportFileRepository extends JpaRepository<ReportFileEntity, UUID> {
    Optional<ReportFileEntity> findByUserAndId(String login, UUID id);
}
