package by.itacademy.mail.scheduler.dao;

import by.itacademy.mail.scheduler.dao.entity.MonthlyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReportEntity, String> {
    Collection<MonthlyReportEntity> findByDtCreateLessThan(LocalDateTime ldt);
}
