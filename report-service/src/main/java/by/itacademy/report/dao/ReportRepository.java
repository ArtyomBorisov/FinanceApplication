package by.itacademy.report.dao;

import by.itacademy.report.dao.entity.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {
    Page<ReportEntity> findByUserOrderByDtCreateAsc(String login, Pageable pageable);
    Optional<ReportEntity> findByUserAndIdAndStatus(String login, UUID id, String status);

    @Modifying
    @Query("Update ReportEntity r set r.status = ?2 where r.id = ?1")
    int setStatusForReportEntity(UUID id, String status);
}
