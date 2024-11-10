package org.indoles.autionserviceserver.core.auction.repository;

import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<AuctionEntity, Long> {

    Optional<AuctionEntity> findById(Long id);

    Page<AuctionEntity> findAll(Pageable pageable);

    Page<AuctionEntity> findAllBySellerId(long sellerId, Pageable pageable);

    Optional<AuctionEntity> findByIdAndUpdatedAt(Long auctionId, LocalDateTime updatedAt);
}
