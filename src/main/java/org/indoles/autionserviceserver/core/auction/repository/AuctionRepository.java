package org.indoles.autionserviceserver.core.auction.repository;

import jakarta.persistence.LockModeType;
import org.indoles.autionserviceserver.core.auction.domain.AuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.domain.SellerAuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<AuctionEntity, Long> {

    Optional<AuctionEntity> findById(Long id);

    //List<AuctionEntity> findAllBy(AuctionSearchCondition condition);

    //List<AuctionEntity> findAllBy(SellerAuctionSearchCondition condition);

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    //Optional<AuctionEntity> findByIdForUpdate(long auctionId);
}
