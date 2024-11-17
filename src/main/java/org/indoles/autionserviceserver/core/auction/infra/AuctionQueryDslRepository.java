package org.indoles.autionserviceserver.core.auction.infra;

import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.SellerAuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;

import java.util.List;

public interface AuctionQueryDslRepository {

    List<AuctionEntity> findAllBy(AuctionSearchConditionRequest condition);

    List<AuctionEntity> findAllBy(SellerAuctionSearchConditionRequest condition);

}