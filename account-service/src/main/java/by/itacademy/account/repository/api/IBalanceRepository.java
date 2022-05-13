package by.itacademy.account.repository.api;

import by.itacademy.account.repository.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IBalanceRepository extends JpaRepository<BalanceEntity, UUID> {
}
