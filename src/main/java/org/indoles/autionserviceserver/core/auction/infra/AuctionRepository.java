package org.indoles.autionserviceserver.core.auction.infra;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.SellerAuctionSearchCondition;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository {

    Auction save(Auction auction);

    Optional<Auction> findById(Long id);

    void deleteById(long id);

    List<Auction> findAllBy(AuctionSearchConditionRequest condition);

    List<Auction> findAllBy(SellerAuctionSearchCondition condition);

    Optional<Auction> findByIdForUpdate(long auctionId);
}
