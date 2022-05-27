package by.itacademy.report.repository.api;

import by.itacademy.report.repository.entity.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IReportRepository extends JpaRepository<ReportEntity, UUID> {
    Page<ReportEntity> findByOrderByDtCreateAsc(Pageable pageable);
    Optional<ReportEntity> findByIdAndStatus(UUID id, String status);
}
