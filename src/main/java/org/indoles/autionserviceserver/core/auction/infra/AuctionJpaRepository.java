package org.indoles.autionserviceserver.core.auction.infra;

import jakarta.persistence.LockModeType;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuctionJpaRepository extends JpaRepository<AuctionEntity, Long>, AuctionQueryDslRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AuctionEntity a where a.id = :id")
    Optional<AuctionEntity> findByIdForUpdate(Long id);
}
