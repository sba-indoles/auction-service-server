package org.indoles.autionserviceserver.core.auction.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.indoles.autionserviceserver.core.auction.dto.AuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.dto.SellerAuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.indoles.autionserviceserver.core.auction.entity.QAuctionEntity;

import java.util.List;

@RequiredArgsConstructor
public class AuctionQueryDslRepositoryImpl implements AuctionQueryDslRepository {

    private final JPAQueryFactory query;

    @Override
    public List<AuctionEntity> findAllBy(AuctionSearchCondition condition) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        return query
                .select(auction)
                .from(auction)
                .orderBy(auction.id.desc())
                .limit(condition.size())
                .offset(condition.offset())
                .fetch();
    }

    @Override
    public List<AuctionEntity> findAllBy(SellerAuctionSearchCondition condition) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        return query
                .select(auction)
                .from(auction)
                .where(auction.sellerId.eq(condition.sellerId()))
                .orderBy(auction.id.desc())
                .limit(condition.size())
                .offset(condition.offset())
                .fetch();
    }
}

