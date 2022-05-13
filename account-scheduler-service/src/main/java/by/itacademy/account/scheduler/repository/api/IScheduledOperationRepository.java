package by.itacademy.account.scheduler.repository.api;

import by.itacademy.account.scheduler.repository.entity.ScheduledOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IScheduledOperationRepository extends JpaRepository<ScheduledOperationEntity, UUID> {
    List<ScheduledOperationEntity> findByOrderByDtCreateAsc();
}
